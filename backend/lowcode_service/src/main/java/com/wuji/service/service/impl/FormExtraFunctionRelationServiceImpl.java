package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.service.converter.AbstractFormExtraFunctionRelationConverter;
import com.wuji.service.mapper.FormExtraFunctionRelationMapper;
import com.wuji.service.model.entity.FormExtraFunctionRelationEntity;
import com.wuji.service.model.vo.FormExtraFunctionRelationVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionRelationVO;
import com.wuji.service.service.FormExtraFunctionRelationService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.TemplateFormExtraFunctionRelationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-12-25
 */
@Service
public class FormExtraFunctionRelationServiceImpl
        extends ServiceImpl<FormExtraFunctionRelationMapper, FormExtraFunctionRelationEntity>
        implements FormExtraFunctionRelationService {

    @Autowired
    private FormExtraFunctionRelationMapper formExtraFunctionRelationMapper;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Autowired
    private TemplateFormExtraFunctionRelationService templateFormExtraFunctionRelationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String functionId, String businessType, List<String> businessIdList) {
        LambdaQueryWrapper<FormExtraFunctionRelationEntity> delete = new LambdaQueryWrapper<>();
        delete.eq(FormExtraFunctionRelationEntity::getFunctionId, functionId);
        formExtraFunctionRelationMapper.delete(delete);
        if (CollectionUtils.isEmpty(businessIdList)) {
            return;
        }
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList =
                buildEntityList(functionId, businessType, businessIdList);
        saveBatch(formExtraFunctionRelationEntityList);
    }

    private List<FormExtraFunctionRelationEntity> buildEntityList(String functionId, String businessType,
                                                                  List<String> businessIdList) {
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList = new ArrayList<>();
        for (String businessId : businessIdList) {
            FormExtraFunctionRelationEntity formExtraFunctionRelationEntity = new FormExtraFunctionRelationEntity();
            formExtraFunctionRelationEntity.setFunctionId(functionId);
            formExtraFunctionRelationEntity.setBusinessId(businessId);
            formExtraFunctionRelationEntity.setBusinessType(businessType);
            formExtraFunctionRelationEntityList.add(formExtraFunctionRelationEntity);
        }
        return formExtraFunctionRelationEntityList;
    }

    @Override
    public List<FormExtraFunctionRelationVO> getByFunctionIdList(List<String> functionIdList, String applicationId) {
        if (CollectionUtils.isEmpty(functionIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormExtraFunctionRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormExtraFunctionRelationEntity::getFunctionId, functionIdList);
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList =
                formExtraFunctionRelationMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formExtraFunctionRelationEntityList)) {
            return new ArrayList<>();
        }
        List<String> privilegeIdList =
                formExtraFunctionRelationEntityList.stream().map(FormExtraFunctionRelationEntity::getBusinessId)
                        .collect(Collectors.toList());
        List<FormPrivilegeVO> formPrivilegeVOS = formPrivilegeService.getByIdList(privilegeIdList, applicationId);
        Map<String, String> groupNameMap = formPrivilegeVOS.stream()
                .collect(Collectors.toMap(FormPrivilegeVO::getId, FormPrivilegeVO::getGroupName));
        List<FormExtraFunctionRelationVO> formExtraFunctionRelationVOList = new ArrayList<>();
        for (FormExtraFunctionRelationEntity formExtraFunctionRelationEntity : formExtraFunctionRelationEntityList) {
            FormExtraFunctionRelationVO formExtraFunctionRelationVO =
                    AbstractFormExtraFunctionRelationConverter.INSTANCE.toVO(formExtraFunctionRelationEntity);
            String name = groupNameMap.get(formExtraFunctionRelationEntity.getBusinessId());
            if (StringUtils.isNotEmpty(name)) {
                formExtraFunctionRelationVO.setBusinessName(name);
                formExtraFunctionRelationVOList.add(formExtraFunctionRelationVO);
            }
        }
        return formExtraFunctionRelationVOList;
    }

    @Override
    public List<FormExtraFunctionRelationVO> getByBusinessId(String businessId, String businessType) {
        LambdaQueryWrapper<FormExtraFunctionRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionRelationEntity::getBusinessId, businessId);
        queryWrapper.eq(FormExtraFunctionRelationEntity::getBusinessType, businessType);
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList =
                formExtraFunctionRelationMapper.selectList(queryWrapper);

        List<FormExtraFunctionRelationVO> formExtraFunctionRelationVOList = new ArrayList<>();
        for (FormExtraFunctionRelationEntity formExtraFunctionRelationEntity : formExtraFunctionRelationEntityList) {
            FormExtraFunctionRelationVO formExtraFunctionRelationVO =
                    AbstractFormExtraFunctionRelationConverter.INSTANCE.toVO(formExtraFunctionRelationEntity);
            formExtraFunctionRelationVOList.add(formExtraFunctionRelationVO);
        }
        return formExtraFunctionRelationVOList;
    }

    @Override
    public List<FormExtraFunctionRelationVO> getByBusinessIds(List<String> businessIds, String businessType) {
        if (CollectionUtils.isEmpty(businessIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormExtraFunctionRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormExtraFunctionRelationEntity::getBusinessId, businessIds);
        queryWrapper.eq(StringUtils.isNotEmpty(businessType), FormExtraFunctionRelationEntity::getBusinessType,
                businessType);
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList =
                formExtraFunctionRelationMapper.selectList(queryWrapper);

        List<FormExtraFunctionRelationVO> formExtraFunctionRelationVOList = new ArrayList<>();
        for (FormExtraFunctionRelationEntity formExtraFunctionRelationEntity : formExtraFunctionRelationEntityList) {
            FormExtraFunctionRelationVO formExtraFunctionRelationVO =
                    AbstractFormExtraFunctionRelationConverter.INSTANCE.toVO(formExtraFunctionRelationEntity);
            formExtraFunctionRelationVOList.add(formExtraFunctionRelationVO);
        }
        return formExtraFunctionRelationVOList;
    }

    @Override
    public void copy(Map<String, String> functionIdMap, Map<String, String> privilegeMap) {
        Collection<String> functionIdList = functionIdMap.keySet();
        if (CollectionUtils.isEmpty(functionIdList)) {
            return;
        }
        LambdaQueryWrapper<FormExtraFunctionRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormExtraFunctionRelationEntity::getFunctionId, functionIdList);
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList =
                formExtraFunctionRelationMapper.selectList(queryWrapper);
        for (FormExtraFunctionRelationEntity formExtraFunctionRelationEntity : formExtraFunctionRelationEntityList) {
            formExtraFunctionRelationEntity.setId(null);
            formExtraFunctionRelationEntity.setFunctionId(
                    functionIdMap.get(formExtraFunctionRelationEntity.getFunctionId()));
            formExtraFunctionRelationEntity.setBusinessId(
                    privilegeMap.get(formExtraFunctionRelationEntity.getBusinessId()));
        }
        if (CollectionUtils.isNotEmpty(formExtraFunctionRelationEntityList)) {
            saveBatch(formExtraFunctionRelationEntityList);
        }
    }

    @Override
    public void useTemplate(Map<String, String> functionIdMap, Map<String, String> privilegeMap) {
        List<String> functionIdList = new ArrayList<>(functionIdMap.keySet());
        if (CollectionUtils.isEmpty(functionIdList)) {
            return;
        }
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList = new ArrayList<>();
        List<TemplateFormExtraFunctionRelationVO> templateFormExtraFunctionRelationServiceByFunctionIdList =
                templateFormExtraFunctionRelationService.getByFunctionIdList(functionIdList);
        for (TemplateFormExtraFunctionRelationVO templateFormExtraFunctionRelationVO : templateFormExtraFunctionRelationServiceByFunctionIdList) {
            FormExtraFunctionRelationEntity formExtraFunctionRelationEntity =
                    AbstractFormExtraFunctionRelationConverter.INSTANCE.toEntity(templateFormExtraFunctionRelationVO);
            formExtraFunctionRelationEntity.setFunctionId(
                    functionIdMap.get(formExtraFunctionRelationEntity.getFunctionId()));
            formExtraFunctionRelationEntity.setBusinessId(
                    privilegeMap.get(formExtraFunctionRelationEntity.getBusinessId()));
            formExtraFunctionRelationEntityList.add(formExtraFunctionRelationEntity);
        }
        if (CollectionUtils.isNotEmpty(formExtraFunctionRelationEntityList)) {
            saveBatch(formExtraFunctionRelationEntityList);
        }
    }

    @Override
    public void useTemplateDefault(List<TemplateFormExtraFunctionRelationVO> formExtraFunctionRelationList) {
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList = new ArrayList<>();
        for (TemplateFormExtraFunctionRelationVO formExtraFunctionRelationVO : formExtraFunctionRelationList) {
            FormExtraFunctionRelationEntity formExtraFunctionRelationEntity =
                    AbstractFormExtraFunctionRelationConverter.INSTANCE.toEntity(formExtraFunctionRelationVO);
            formExtraFunctionRelationEntityList.add(formExtraFunctionRelationEntity);
        }
        if (CollectionUtils.isNotEmpty(formExtraFunctionRelationEntityList)) {
            saveBatch(formExtraFunctionRelationEntityList);
        }
    }

    @Override
    public void saveByButton(String privilegeId, List<String> functionIdList) {
        LambdaQueryWrapper<FormExtraFunctionRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionRelationEntity::getBusinessId, privilegeId);
        queryWrapper.eq(FormExtraFunctionRelationEntity::getBusinessType, "PRIVILEGE");
        formExtraFunctionRelationMapper.delete(queryWrapper);
        if (CollectionUtils.isEmpty(functionIdList)) {
            return;
        }
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList = new ArrayList<>();
        for (String functionId : functionIdList) {
            FormExtraFunctionRelationEntity formExtraFunctionRelationEntity = new FormExtraFunctionRelationEntity();
            formExtraFunctionRelationEntity.setFunctionId(functionId);
            formExtraFunctionRelationEntity.setBusinessId(privilegeId);
            formExtraFunctionRelationEntity.setBusinessType("PRIVILEGE");
            formExtraFunctionRelationEntityList.add(formExtraFunctionRelationEntity);
        }
        saveBatch(formExtraFunctionRelationEntityList);
    }

    @Override
    public List<String> getByPrivilegeId(String privilegeId) {
        LambdaQueryWrapper<FormExtraFunctionRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionRelationEntity::getBusinessId, privilegeId);
        queryWrapper.eq(FormExtraFunctionRelationEntity::getBusinessType, "PRIVILEGE");
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList =
                formExtraFunctionRelationMapper.selectList(queryWrapper);
        return formExtraFunctionRelationEntityList.stream().map(FormExtraFunctionRelationEntity::getFunctionId)
                .collect(Collectors.toList());
    }
}
