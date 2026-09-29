package com.wuji.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.workflow.converter.AbstractFlowableCopyConverter;
import com.wuji.workflow.mapper.FlowableCopyMapper;
import com.wuji.workflow.model.domain.FlowableCopyDomain;
import com.wuji.workflow.model.entity.FlowableCopyEntity;
import com.wuji.workflow.model.request.FlowableCopyCreateRequest;
import com.wuji.workflow.model.request.FlowableCopyRequest;
import com.wuji.workflow.model.vo.FlowableCopyVO;
import com.wuji.workflow.service.FlowableActivityConfigService;
import com.wuji.workflow.service.FlowableCopyService;
import com.wuji.workflow.service.FlowableCopyUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 抄送表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-12
 */
@Service
public class FlowableCopyServiceImpl extends ServiceImpl<FlowableCopyMapper, FlowableCopyEntity>
        implements FlowableCopyService {

    @Autowired
    private FlowableCopyMapper flowableCopyMapper;

    @Autowired
    private FlowableCopyUserService flowableCopyUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private FlowableActivityConfigService flowableActivityConfigService;

    @Override
    public void create(FlowableCopyCreateRequest flowableCopyCreateRequest) {
        List<Long> userIdList = flowableActivityConfigService.getCopyUser(flowableCopyCreateRequest.getActivityId(),
                flowableCopyCreateRequest.getModelId(), flowableCopyCreateRequest.getProcessVariables(),
                flowableCopyCreateRequest.getInstValue());
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        // LambdaQueryWrapper<FlowableCopyEntity> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.eq(FlowableCopyEntity::getProcessInstanceId, flowableCopyCreateRequest.getProcessInstanceId());
        // queryWrapper.eq(FlowableCopyEntity::getActivityId, flowableCopyCreateRequest.getActivityId());
        // queryWrapper.eq(FlowableCopyEntity::getDataUuid, flowableCopyCreateRequest.getDataUuid());
        // List<FlowableCopyEntity> flowableCopyEntities = flowableCopyMapper.selectList(queryWrapper);
        // if (CollectionUtils.isNotEmpty(flowableCopyEntities)) {
        //     return;
        // }
        FlowableCopyEntity flowableCopyEntity =
                AbstractFlowableCopyConverter.INSTANCE.toEntity(flowableCopyCreateRequest);
        flowableCopyMapper.insert(flowableCopyEntity);
        flowableCopyUserService.create(flowableCopyEntity.getId(), userIdList);
    }

    @Override
    public QueryPageVO<FlowableCopyVO> queryList(FlowableCopyRequest flowableCopyRequest) {
        QueryWrapper<FlowableCopyDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fcu.user_id", UserUtils.getUser().getUserId());
        queryWrapper.eq("fc.company_id", UserUtils.getUser().getCompanyId());
        queryWrapper.eq(flowableCopyRequest.getId() != null, "fc.id", flowableCopyRequest.getId());
        queryWrapper.eq(StringUtils.isNotEmpty(flowableCopyRequest.getApplicationId()), "fc.application_id",
                flowableCopyRequest.getApplicationId());
        queryWrapper.eq(flowableCopyRequest.getUserView() != null, "fcu.user_view", flowableCopyRequest.getUserView());
        queryWrapper.orderByDesc("fc.create_time");
        Page<FlowableCopyDomain> page = new Page<>(flowableCopyRequest.getPageNum(), flowableCopyRequest.getPageSize());
        IPage<FlowableCopyDomain> flowableCopyDomainIPage = flowableCopyMapper.selectPage(page, queryWrapper);
        List<FlowableCopyDomain> flowableCopyDomainList = flowableCopyDomainIPage.getRecords();
        List<Long> initiatorList =
                flowableCopyDomainList.stream().map(c -> Long.valueOf(c.getInitiator())).collect(Collectors.toList());
        Map<Long, String> userNameMap = userService.getIdToNameMap(initiatorList);
        List<FlowableCopyVO> flowableCopyVOList = new ArrayList<>();
        for (FlowableCopyDomain flowableCopyDomain : flowableCopyDomainList) {
            FlowableCopyVO flowableCopyVO = AbstractFlowableCopyConverter.INSTANCE.toVO(flowableCopyDomain);
            flowableCopyVO.setInitiatorName(userNameMap.get(Long.valueOf(flowableCopyVO.getInitiator())));
            flowableCopyVOList.add(flowableCopyVO);
        }
        return PageUtils.toQueryPage(flowableCopyDomainIPage, flowableCopyVOList);
    }

    @Override
    public void clear(String applicationId) {
        LambdaQueryWrapper<FlowableCopyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableCopyEntity::getApplicationId, applicationId);
        List<FlowableCopyEntity> flowableCopyEntities = flowableCopyMapper.selectList(queryWrapper);
        flowableCopyMapper.delete(queryWrapper);
        List<Long> copyIdList =
                flowableCopyEntities.stream().map(FlowableCopyEntity::getId).collect(Collectors.toList());
        flowableCopyUserService.clearByCopyId(copyIdList);
    }

    @Override
    public void clear(String applicationId, String formId) {
        LambdaQueryWrapper<FlowableCopyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableCopyEntity::getApplicationId, applicationId);
        queryWrapper.eq(FlowableCopyEntity::getFormId, formId);
        List<FlowableCopyEntity> flowableCopyEntities = flowableCopyMapper.selectList(queryWrapper);
        flowableCopyMapper.delete(queryWrapper);
        List<Long> copyIdList =
                flowableCopyEntities.stream().map(FlowableCopyEntity::getId).collect(Collectors.toList());
        flowableCopyUserService.clearByCopyId(copyIdList);
    }
}
