package com.wuji.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuji.workflow.enums.WorkflowResultCode;
import com.wuji.workflow.exception.FlowableBizException;
import com.wuji.workflow.mapper.FlowableConfigMapper;
import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import com.wuji.workflow.model.entity.FlowableConfigEntity;
import com.wuji.workflow.model.vo.FlowableConfigVO;
import com.wuji.workflow.service.FlowableActivityConfigService;
import com.wuji.workflow.service.FlowableConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-24
 */
@Service
public class FlowableConfigServiceImpl extends ServiceImpl<FlowableConfigMapper, FlowableConfigEntity>
        implements FlowableConfigService {

    @Autowired
    private FlowableConfigMapper flowableConfigMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlowableActivityConfigService flowableActivityConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String modelId, FormModelDesignerDomain formModelDesignerDomain) {
        LambdaQueryWrapper<FlowableConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableConfigEntity::getModelId, modelId);
        FlowableConfigEntity flowableConfigEntity = flowableConfigMapper.selectOne(queryWrapper);
        if (flowableConfigEntity == null) {
            flowableConfigEntity = new FlowableConfigEntity();
        }
        flowableConfigEntity.setModelId(modelId);
        try {
            String config = objectMapper.writer().writeValueAsString(formModelDesignerDomain);
            flowableConfigEntity.setConfig(config);
            saveOrUpdate(flowableConfigEntity);
            flowableActivityConfigService.save(modelId, formModelDesignerDomain.getFlowableTaskConfigList());
        } catch (Exception e) {
            log.error("流程配置json转换失败", e);
            throw new FlowableBizException(WorkflowResultCode.FLOWABLE_DESIGN_FAIL);
        }
    }

    @Override
    public void delete(List<String> modelIdList) {
        if (CollectionUtils.isEmpty(modelIdList)) {
            return;
        }
        LambdaQueryWrapper<FlowableConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FlowableConfigEntity::getModelId, modelIdList);
        flowableConfigMapper.delete(queryWrapper);
        flowableActivityConfigService.delete(modelIdList);
    }

    @Override
    public FlowableConfigVO getByModelId(String modelId) {
        LambdaQueryWrapper<FlowableConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableConfigEntity::getModelId, modelId);
        FlowableConfigEntity flowableConfigEntity = flowableConfigMapper.selectOne(queryWrapper);
        FlowableConfigVO flowableConfigVO = new FlowableConfigVO();
        if (flowableConfigEntity != null && StringUtils.isNotEmpty(flowableConfigEntity.getConfig())) {
            try {
                FormModelDesignerDomain formModelDesignerDomain =
                        objectMapper.readValue(flowableConfigEntity.getConfig(), FormModelDesignerDomain.class);
                formModelDesignerDomain.setFlowableTaskConfigList(flowableActivityConfigService.detail(modelId, null));
                flowableConfigVO.setConfig(formModelDesignerDomain);
            } catch (Exception e) {
                log.error("流程配置json转换失败", e);
            }
            flowableConfigVO.setModelId(flowableConfigEntity.getModelId());
        }
        return flowableConfigVO;
    }
}
