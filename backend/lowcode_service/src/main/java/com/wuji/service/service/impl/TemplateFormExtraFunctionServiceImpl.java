package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.entity.BaseUuidEntity;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractTemplateFormExtraFunctionConverter;
import com.wuji.service.mapper.TemplateFormExtraFunctionMapper;
import com.wuji.service.model.entity.TemplateFormExtraFunctionEntity;
import com.wuji.service.model.vo.FormExtraFunctionRelationVO;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionRelationVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionVO;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.TemplateFormExtraFunctionRelationService;
import com.wuji.service.service.TemplateFormExtraFunctionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-01-20
 */
@Service
public class TemplateFormExtraFunctionServiceImpl
        extends ServiceImpl<TemplateFormExtraFunctionMapper, TemplateFormExtraFunctionEntity>
        implements TemplateFormExtraFunctionService {

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Autowired
    private TemplateFormExtraFunctionRelationService templateFormExtraFunctionRelationService;

    @Autowired
    private TemplateFormExtraFunctionMapper templateFormExtraFunctionMapper;


    @Override
    public void generateTemplate(String applicationId, String templateApplicationId,
                                 Map<String, String> privilegeIdMap, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormExtraFunctionEntity::getApplicationId, templateApplicationId);
            templateFormExtraFunctionMapper.delete(queryWrapper);
        }
        List<FormExtraFunctionVO> formExtraFunctionVOList = formExtraFunctionServiceImpl.getByApplicationId(applicationId);
        List<TemplateFormExtraFunctionEntity> insertList = new ArrayList<>();
        Map<String, String> functionIdMap = new HashMap<>();
        List<FormExtraFunctionRelationVO> formExtraFunctionRelationVOList = new ArrayList<>();
        for (FormExtraFunctionVO formExtraFunctionVO : formExtraFunctionVOList) {
            TemplateFormExtraFunctionEntity templateFormExtraFunctionEntity =
                    AbstractTemplateFormExtraFunctionConverter.INSTANCE.toEntity(formExtraFunctionVO);
            String guid = ObjectId.getGuid();
            functionIdMap.put(formExtraFunctionVO.getId(), guid);
            templateFormExtraFunctionEntity.setApplicationId(templateApplicationId);
            templateFormExtraFunctionEntity.setId(guid);
            templateFormExtraFunctionEntity.setCreator(UserUtils.getUser().getNickName());
            templateFormExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
            insertList.add(templateFormExtraFunctionEntity);
            if (CollectionUtils.isNotEmpty(formExtraFunctionVO.getFormExtraFunctionRelationList())) {
                formExtraFunctionRelationVOList.addAll(formExtraFunctionVO.getFormExtraFunctionRelationList());
            }
        }
        templateFormExtraFunctionRelationService.generateTemplate(functionIdMap, formExtraFunctionRelationVOList,
                privilegeIdMap);
        saveBatch(insertList);
    }

    @Override
    public List<TemplateFormExtraFunctionVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormExtraFunctionEntity::getApplicationId, applicationId);
        queryWrapper.eq(TemplateFormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        List<TemplateFormExtraFunctionEntity> formExtraFunctionEntityList =
                templateFormExtraFunctionMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formExtraFunctionEntityList)) {
            return new ArrayList<>();
        }
        List<TemplateFormExtraFunctionVO> formExtraFunctionList = new ArrayList<>();
        List<String> idList =
                formExtraFunctionEntityList.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
        List<TemplateFormExtraFunctionRelationVO> formExtraFunctionRelationVOList =
                templateFormExtraFunctionRelationService.getByFunctionIdList(idList);
        Map<String, List<TemplateFormExtraFunctionRelationVO>> functionMap = formExtraFunctionRelationVOList.stream()
                .collect(Collectors.groupingBy(TemplateFormExtraFunctionRelationVO::getFunctionId));
        for (TemplateFormExtraFunctionEntity formExtraFunctionEntity : formExtraFunctionEntityList) {
            TemplateFormExtraFunctionVO formExtraFunctionVO =
                    AbstractTemplateFormExtraFunctionConverter.INSTANCE.toVO(formExtraFunctionEntity);
            formExtraFunctionVO.setFormExtraFunctionRelationList(functionMap.get(formExtraFunctionEntity.getId()));
            formExtraFunctionList.add(formExtraFunctionVO);
        }
        return formExtraFunctionList;
    }
}
