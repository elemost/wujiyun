package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.service.converter.AbstractTemplateFormExtraFunctionRelationConverter;
import com.wuji.service.mapper.TemplateFormExtraFunctionRelationMapper;
import com.wuji.service.model.entity.TemplateFormExtraFunctionRelationEntity;
import com.wuji.service.model.vo.FormExtraFunctionRelationVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionRelationVO;
import com.wuji.service.service.TemplateFormExtraFunctionRelationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-01-20
 */
@Service
public class TemplateFormExtraFunctionRelationServiceImpl
        extends ServiceImpl<TemplateFormExtraFunctionRelationMapper, TemplateFormExtraFunctionRelationEntity>
        implements TemplateFormExtraFunctionRelationService {

    @Autowired
    private TemplateFormExtraFunctionRelationMapper templateFormExtraFunctionRelationMapper;

    @Override
    public void generateTemplate(Map<String, String> functionIdMap,
                                 List<FormExtraFunctionRelationVO> formExtraFunctionRelationList,
                                 Map<String, String> privilegeIdMap) {

        List<TemplateFormExtraFunctionRelationEntity> templateFormExtraFunctionRelationEntityList = new ArrayList<>();
        for (FormExtraFunctionRelationVO formExtraFunctionRelationVO : formExtraFunctionRelationList) {
            TemplateFormExtraFunctionRelationEntity templateFormExtraFunctionRelationEntity =
                    AbstractTemplateFormExtraFunctionRelationConverter.INSTANCE.toEntity(formExtraFunctionRelationVO);
            templateFormExtraFunctionRelationEntity.setFunctionId(
                    functionIdMap.get(formExtraFunctionRelationVO.getFunctionId()));
            String businessId = privilegeIdMap.get(formExtraFunctionRelationVO.getBusinessId());
            if (StringUtils.isEmpty(businessId)) {
                continue;
            }
            templateFormExtraFunctionRelationEntity.setBusinessId(businessId);
            templateFormExtraFunctionRelationEntityList.add(templateFormExtraFunctionRelationEntity);
        }
        if (CollectionUtils.isNotEmpty(templateFormExtraFunctionRelationEntityList)) {
            saveBatch(templateFormExtraFunctionRelationEntityList);
        }
    }

    @Override
    public List<TemplateFormExtraFunctionRelationVO> getByFunctionIdList(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<TemplateFormExtraFunctionRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TemplateFormExtraFunctionRelationEntity::getFunctionId, idList);
        List<TemplateFormExtraFunctionRelationEntity> templateFormExtraFunctionRelationEntityList =
                templateFormExtraFunctionRelationMapper.selectList(queryWrapper);
        List<TemplateFormExtraFunctionRelationVO> templateFormExtraFunctionRelationVOS = new ArrayList<>();
        for (TemplateFormExtraFunctionRelationEntity templateFormExtraFunctionRelationEntity : templateFormExtraFunctionRelationEntityList) {
            TemplateFormExtraFunctionRelationVO formExtraFunctionRelationVO =
                    AbstractTemplateFormExtraFunctionRelationConverter.INSTANCE.toVO(
                            templateFormExtraFunctionRelationEntity);
            templateFormExtraFunctionRelationVOS.add(formExtraFunctionRelationVO);
        }
        return templateFormExtraFunctionRelationVOS;
    }
}
