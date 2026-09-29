package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wuji.admin.enums.UserTypeEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.entity.BaseUuidEntity;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractFormPrivilegeConverter;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.enums.FormPrivilegeButtonEnum;
import com.wuji.service.enums.FormPrivilegeDataScopeTypeEnum;
import com.wuji.service.enums.FormPrivilegeUserPrivilegeEnum;
import com.wuji.service.enums.MongodbSearchConditionQuoteTypeEnum;
import com.wuji.service.mapper.FormPrivilegeMapper;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.entity.FormPrivilegeEntity;
import com.wuji.service.model.info.FormPrivilegeDataScope;
import com.wuji.service.model.info.FormPrivilegeFieldConfig;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.request.FormPrivilegeCreateRequest;
import com.wuji.service.model.request.FormPrivilegeSortRequest;
import com.wuji.service.model.request.FormPrivilegeUpdateRequest;
import com.wuji.service.model.vo.FormPrivilegeConfigVO;
import com.wuji.service.model.vo.FormPrivilegeDetailVO;
import com.wuji.service.model.vo.FormPrivilegeUserVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeVO;
import com.wuji.service.service.FormExtraFunctionRelationService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormPrivilegeUserService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.TemplateFormPrivilegeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
 * @since 2024-10-15
 */
@Service
public class FormPrivilegeServiceImpl extends ServiceImpl<FormPrivilegeMapper, FormPrivilegeEntity>
        implements FormPrivilegeService {

    @Autowired
    private FormPrivilegeUserService formPrivilegeUserService;

    @Autowired
    private FormPrivilegeMapper formPrivilegeMapper;

    @Autowired
    private TemplateFormPrivilegeService templateFormPrivilegeService;

    @Autowired
    private FormService formService;

    @Autowired
    private FormExtraFunctionRelationService formExtraFunctionRelationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(FormPrivilegeCreateRequest formPrivilegeCreateRequest) {
        UserDomain user = UserUtils.getUser();
        FormPrivilegeEntity formPrivilegeEntity =
                AbstractFormPrivilegeConverter.INSTANCE.toEntity(formPrivilegeCreateRequest);
        if (formPrivilegeCreateRequest.getOperatePrivilegeList() != null) {
            formPrivilegeEntity.setOperatePrivilege(
                    JSONArray.toJSONString(formPrivilegeCreateRequest.getOperatePrivilegeList()));
        }
        if (formPrivilegeCreateRequest.getViewPrivilegeList() != null) {
            formPrivilegeEntity.setViewPrivilege(
                    JSONArray.toJSONString(formPrivilegeCreateRequest.getViewPrivilegeList()));
        }
        if (formPrivilegeCreateRequest.getDataScopeList() != null) {
            formPrivilegeEntity.setDataScope(JSONArray.toJSONString(formPrivilegeCreateRequest.getDataScopeList()));
        }
        if (formPrivilegeCreateRequest.getOperateFieldPrivilegeList() != null) {
            formPrivilegeEntity.setOperateFieldPrivilege(
                    JSONArray.toJSONString(formPrivilegeCreateRequest.getOperateFieldPrivilegeList()));
        }
        if (formPrivilegeCreateRequest.getFieldPrivilegeList() != null) {
            formPrivilegeEntity.setFieldPrivilege(
                    JSONArray.toJSONString(formPrivilegeCreateRequest.getFieldPrivilegeList()));
        }

        formPrivilegeEntity.setId(ObjectId.getGuid());
        formPrivilegeEntity.setCreatorName(user.getNickName());
        formPrivilegeEntity.setModifierName(user.getNickName());
        formPrivilegeMapper.insert(formPrivilegeEntity);
        formPrivilegeUserService.save(formPrivilegeCreateRequest.getApplicationId(),
                formPrivilegeCreateRequest.getCategoryId(), formPrivilegeEntity.getId(),
                formPrivilegeCreateRequest.getFormPrivilegeUserList());
        formExtraFunctionRelationService.saveByButton(formPrivilegeEntity.getId(),
                formPrivilegeCreateRequest.getButtonList());
        return formPrivilegeEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FormPrivilegeUpdateRequest formPrivilegeUpdateRequest) {
        UserDomain user = UserUtils.getUser();
        FormPrivilegeEntity formPrivilegeEntity =
                AbstractFormPrivilegeConverter.INSTANCE.toEntity(formPrivilegeUpdateRequest);
        formPrivilegeEntity.setModifierName(user.getNickName());
        if (formPrivilegeUpdateRequest.getOperatePrivilegeList() != null) {
            formPrivilegeEntity.setOperatePrivilege(
                    JSONArray.toJSONString(formPrivilegeUpdateRequest.getOperatePrivilegeList()));
        }
        if (formPrivilegeUpdateRequest.getViewPrivilegeList() != null) {
            formPrivilegeEntity.setViewPrivilege(
                    JSONArray.toJSONString(formPrivilegeUpdateRequest.getViewPrivilegeList()));
        }
        if (formPrivilegeUpdateRequest.getDataScopeList() != null) {
            formPrivilegeEntity.setDataScope(JSONArray.toJSONString(formPrivilegeUpdateRequest.getDataScopeList()));
        }
        if (formPrivilegeUpdateRequest.getOperateFieldPrivilegeList() != null) {
            formPrivilegeEntity.setOperateFieldPrivilege(
                    JSONArray.toJSONString(formPrivilegeUpdateRequest.getOperateFieldPrivilegeList()));
        }
        if (formPrivilegeUpdateRequest.getFieldPrivilegeList() != null) {
            formPrivilegeEntity.setFieldPrivilege(
                    JSONArray.toJSONString(formPrivilegeUpdateRequest.getFieldPrivilegeList()));
        }
        LambdaQueryWrapper<FormPrivilegeEntity> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(FormPrivilegeEntity::getId, formPrivilegeUpdateRequest.getId());
        updateWrapper.eq(FormPrivilegeEntity::getApplicationId, formPrivilegeUpdateRequest.getApplicationId());
        formPrivilegeMapper.update(formPrivilegeEntity, updateWrapper);
        formPrivilegeUserService.save(formPrivilegeUpdateRequest.getApplicationId(),
                formPrivilegeUpdateRequest.getCategoryId(), formPrivilegeEntity.getId(),
                formPrivilegeUpdateRequest.getFormPrivilegeUserList());
        // formExtraFunctionRelationService.saveByButton(formPrivilegeEntity.getId(),
        //         formPrivilegeUpdateRequest.getButtonList());
    }

    @Override
    public List<FormPrivilegeVO> getList(String categoryId, String applicationId) {
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getCategoryId, categoryId);
        queryWrapper.eq(FormPrivilegeEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormPrivilegeEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByDesc(FormPrivilegeEntity::getSort);
        queryWrapper.orderByAsc(FormPrivilegeEntity::getId);
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        return getFormPrivilegeVOS(formPrivilegeEntityList);
    }

    @Override
    public List<FormPrivilegeVO> getSelectList(String applicationId) {
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormPrivilegeEntity::getDeleted, Boolean.FALSE);
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        List<FormPrivilegeVO> formPrivilegeVOS = getFormPrivilegeVOS(formPrivilegeEntityList);
        List<FormPrivilegeVO> returnList = new ArrayList<>();
        for (FormPrivilegeVO formPrivilegeVO : formPrivilegeVOS) {
            List<FormPrivilegeDataScope> dataScopeList = formPrivilegeVO.getDataScopeList();
            if (CollectionUtils.isEmpty(dataScopeList)) {
                continue;
            }
            FormPrivilegeDataScope formPrivilegeDataScope = dataScopeList.stream()
                    .filter(c -> FormPrivilegeDataScopeTypeEnum.CUSTOM_FILTER.name().equals(c.getDataScopeType()))
                    .findFirst().orElse(null);
            if (formPrivilegeDataScope == null) {
                returnList.add(formPrivilegeVO);
            } else {
                MongodbSearchFilter filter = formPrivilegeDataScope.getFilter();
                if (filter == null) {
                    returnList.add(formPrivilegeVO);
                    continue;
                }
                List<MongodbSearchCondition> conditionList = filter.getConditionList();
                if (CollectionUtils.isEmpty(conditionList)) {
                    returnList.add(formPrivilegeVO);
                    continue;
                }
                boolean existPrivilege = Boolean.FALSE;
                for (MongodbSearchCondition mongodbSearchCondition : conditionList) {
                    if (MongodbSearchConditionQuoteTypeEnum.PRIVILEGE.name()
                            .equals(mongodbSearchCondition.getQuoteType())) {
                        existPrivilege = true;
                        break;
                    }
                }
                if (!existPrivilege) {
                    returnList.add(formPrivilegeVO);
                }
            }
        }
        return returnList;
    }

    @Override
    public List<FormPrivilegeVO> getByIdList(List<String> idList, String applicationId) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormPrivilegeEntity::getId, idList);
        queryWrapper.eq(FormPrivilegeEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormPrivilegeEntity::getDeleted, Boolean.FALSE);
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        return getFormPrivilegeVOS(formPrivilegeEntityList);
    }

    private List<FormPrivilegeVO> getFormPrivilegeVOS(List<FormPrivilegeEntity> formPrivilegeEntityList) {
        if (CollectionUtils.isEmpty(formPrivilegeEntityList)) {
            return new ArrayList<>();
        }
        List<String> groupList =
                formPrivilegeEntityList.stream().map(FormPrivilegeEntity::getId).collect(Collectors.toList());
        List<FormPrivilegeUserVO> formPrivilegeUserVOList = formPrivilegeUserService.getByGroupIdList(groupList);
        Map<String, List<FormPrivilegeUserVO>> groupIdMap =
                formPrivilegeUserVOList.stream().collect(Collectors.groupingBy(FormPrivilegeUserVO::getGroupId));
        List<FormPrivilegeVO> formPrivilegeVOList = new ArrayList<>();
        for (FormPrivilegeEntity formPrivilegeEntity : formPrivilegeEntityList) {
            FormPrivilegeVO formPrivilegeVO = AbstractFormPrivilegeConverter.INSTANCE.toVO(formPrivilegeEntity);
            formPrivilegeVO.setFormPrivilegeUserList(groupIdMap.get(formPrivilegeEntity.getId()));
            if (StringUtils.isNotEmpty(formPrivilegeEntity.getViewPrivilege())) {
                formPrivilegeVO.setViewPrivilegeList(
                        JSONArray.parseArray(formPrivilegeEntity.getViewPrivilege(), String.class));
            }
            if (StringUtils.isNotEmpty(formPrivilegeEntity.getOperatePrivilege())) {
                formPrivilegeVO.setOperatePrivilegeList(
                        JSONArray.parseArray(formPrivilegeEntity.getOperatePrivilege(), String.class));
            }
            if (StringUtils.isNotEmpty(formPrivilegeEntity.getFieldPrivilege())) {
                formPrivilegeVO.setFieldPrivilegeList(
                        JSONArray.parseArray(formPrivilegeEntity.getFieldPrivilege(), FormPrivilegeFieldConfig.class));
            }
            if (StringUtils.isNotEmpty(formPrivilegeEntity.getOperatePrivilege())) {
                formPrivilegeVO.setOperateFieldPrivilegeList(
                        JSONArray.parseArray(formPrivilegeEntity.getOperateFieldPrivilege(),
                                FormPrivilegeFieldConfig.class));
            }
            if (StringUtils.isNotEmpty(formPrivilegeEntity.getDataScope())) {
                formPrivilegeVO.setDataScopeList(
                        JSONArray.parseArray(formPrivilegeEntity.getDataScope(), FormPrivilegeDataScope.class));
            }
            formPrivilegeVOList.add(formPrivilegeVO);
        }
        return formPrivilegeVOList;
    }

    @Override
    public FormPrivilegeVO detail(String id) {
        if (Constants.ADMIN_PRIVILEGE.equals(id)) {
            FormPrivilegeEntity adminDefaultConfig = FormPrivilegeEntity.getAdminDefaultConfig();
            adminDefaultConfig.setId(id);
            return getFormPrivilegeVOS(Collections.singletonList(adminDefaultConfig)).get(0);
        }
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getId, id);
        queryWrapper.eq(FormPrivilegeEntity::getDeleted, Boolean.FALSE);
        FormPrivilegeEntity formPrivilegeEntity = formPrivilegeMapper.selectOne(queryWrapper);
        if (formPrivilegeEntity == null) {
            return null;
        }
        FormPrivilegeVO formPrivilegeVO = getFormPrivilegeVOS(Collections.singletonList(formPrivilegeEntity)).get(0);
        List<String> buttonIdList = formExtraFunctionRelationService.getByPrivilegeId(id);
        formPrivilegeVO.setButtonList(buttonIdList);
        return formPrivilegeVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getCategoryId, id);
        FormPrivilegeEntity formPrivilegeEntity = new FormPrivilegeEntity();
        formPrivilegeEntity.setId(id);
        formPrivilegeEntity.setDeleted(Boolean.TRUE);
        formPrivilegeMapper.updateById(formPrivilegeEntity);
        formPrivilegeUserService.deleted(id);
    }

    @Override
    public List<String> getExistPrivilegeList(List<String> applicationIdList) {
        List<String> categoryIdList = formPrivilegeUserService.getUserPrivilegeList(applicationIdList).stream()
                .map(FormPrivilegeUserVO::getCategoryId).collect(Collectors.toList());
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (UserTypeEnum.EXTERNAL.getCode().equals(UserUtils.getUser().getUserType())) {
            if (CollectionUtils.isNotEmpty(categoryIdList)) {
                queryWrapper.in(FormPrivilegeEntity::getCategoryId, categoryIdList);
            } else {
                return new ArrayList<>();
            }
        } else {
            if (CollectionUtils.isNotEmpty(categoryIdList)) {
                queryWrapper.and(c -> c.in(FormPrivilegeEntity::getCategoryId, categoryIdList).or()
                        .eq(FormPrivilegeEntity::getUserPrivilege, FormPrivilegeUserPrivilegeEnum.ALL.name()));
            } else {
                queryWrapper.eq(FormPrivilegeEntity::getUserPrivilege, FormPrivilegeUserPrivilegeEnum.ALL.name());
            }
        }

        queryWrapper.eq(FormPrivilegeEntity::getDeleted, Boolean.FALSE);
        queryWrapper.in(FormPrivilegeEntity::getApplicationId, applicationIdList);
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        return formPrivilegeEntityList.stream().map(FormPrivilegeEntity::getCategoryId).collect(Collectors.toList());
    }

    @Override
    public List<FormPrivilegeVO> getUserPrivilegeByCategory(List<String> categoryIdList, String applicationId) {
        if (CollectionUtils.isEmpty(categoryIdList)) {
            return new ArrayList<>();
        }
        List<String> groupIdList =
                formPrivilegeUserService.getUserPrivilegeListByCategory(categoryIdList, applicationId).stream()
                        .map(FormPrivilegeUserVO::getGroupId).collect(Collectors.toList());
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(groupIdList)) {
            queryWrapper.and(c -> c.in(FormPrivilegeEntity::getId, groupIdList).or()
                    .eq(FormPrivilegeEntity::getUserPrivilege, FormPrivilegeUserPrivilegeEnum.ALL.name()));
        } else {
            queryWrapper.eq(FormPrivilegeEntity::getUserPrivilege, FormPrivilegeUserPrivilegeEnum.ALL.name());
        }
        queryWrapper.in(FormPrivilegeEntity::getCategoryId, categoryIdList);
        queryWrapper.eq(FormPrivilegeEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormPrivilegeEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByDesc(FormPrivilegeEntity::getSort);
        queryWrapper.orderByAsc(FormPrivilegeEntity::getId);
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formPrivilegeEntityList)) {
            return new ArrayList<>();
        }
        return getFormPrivilegeVOS(formPrivilegeEntityList);
    }

    @Override
    public List<FormPrivilegeConfigVO> getPrivilege(String categoryId, String applicationId) {
        List<FormPrivilegeConfigVO> formPrivilegeConfigVOList = new ArrayList<>();
        List<FormPrivilegeVO> formPrivilegeVOList =
                getUserPrivilegeByCategory(Collections.singletonList(categoryId), applicationId);
        formPrivilegeVOList.forEach(c -> {
            FormPrivilegeConfigVO formPrivilegeConfigVO = AbstractFormPrivilegeConverter.INSTANCE.toVO(c);
            if (CollectionUtils.isNotEmpty(c.getViewPrivilegeList())) {
                formPrivilegeConfigVO.setViewPrivilege(Boolean.TRUE);
            }
            formPrivilegeConfigVOList.add(formPrivilegeConfigVO);
        });
        return formPrivilegeConfigVOList;
    }

    private static FormPrivilegeConfigVO getFormPrivilegeConfigVO() {
        FormPrivilegeConfigVO formPrivilegeConfigVO = new FormPrivilegeConfigVO();
        formPrivilegeConfigVO.setViewPrivilege(Boolean.TRUE);
        formPrivilegeConfigVO.setId(Constants.ADMIN_PRIVILEGE);
        formPrivilegeConfigVO.setOperatePrivilegeList(FormPrivilegeButtonEnum.getAllOperatorPrivilege());
        formPrivilegeConfigVO.setViewPrivilegeList(FormPrivilegeButtonEnum.getAllViewPrivilege());
        formPrivilegeConfigVO.setGroupName("超级管理员权限组");
        return formPrivilegeConfigVO;
    }

    @Override
    public List<FormPrivilegeVO> getCreateList(List<String> applicationIdList) {
        List<FormPrivilegeUserVO> userPrivilegeList = formPrivilegeUserService.getUserPrivilegeList(applicationIdList);
        List<String> groupIdList =
                userPrivilegeList.stream().map(FormPrivilegeUserVO::getGroupId).collect(Collectors.toList());
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getDeleted, Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(groupIdList)) {
            queryWrapper.and(c -> c.in(FormPrivilegeEntity::getId, groupIdList).or()
                    .eq(FormPrivilegeEntity::getUserPrivilege, FormPrivilegeUserPrivilegeEnum.ALL.name()));
        } else {
            queryWrapper.eq(FormPrivilegeEntity::getUserPrivilege, FormPrivilegeUserPrivilegeEnum.ALL.name());
        }
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        List<FormPrivilegeVO> formPrivilegeVOS = getFormPrivilegeVOS(formPrivilegeEntityList);
        return formPrivilegeVOS.stream().filter(c -> CollectionUtils.isNotEmpty(c.getOperatePrivilegeList()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FormPrivilegeDetailVO> getByFormId(String applicationId, List<String> formIdList) {
        if (CollectionUtils.isEmpty(formIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormPrivilegeEntity::getApplicationId, applicationId);
        queryWrapper.in(CollectionUtils.isNotEmpty(formIdList), FormPrivilegeEntity::getCategoryId, formIdList);
        queryWrapper.orderByDesc(FormPrivilegeEntity::getSort);
        queryWrapper.orderByAsc(FormPrivilegeEntity::getId);
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formPrivilegeEntityList)) {
            return new ArrayList<>();
        }
        List<String> groupList =
                formPrivilegeEntityList.stream().map(FormPrivilegeEntity::getId).collect(Collectors.toList());
        List<FormPrivilegeUserVO> formPrivilegeUserVOList = formPrivilegeUserService.getByGroupIdList(groupList);
        Map<String, List<FormPrivilegeUserVO>> groupIdMap =
                formPrivilegeUserVOList.stream().collect(Collectors.groupingBy(FormPrivilegeUserVO::getGroupId));
        List<FormPrivilegeDetailVO> formPrivilegeDetailVOList = new ArrayList<>();
        for (FormPrivilegeEntity formPrivilegeEntity : formPrivilegeEntityList) {
            FormPrivilegeDetailVO formPrivilegeDetailVO =
                    AbstractFormPrivilegeConverter.INSTANCE.toDetailVO(formPrivilegeEntity);
            formPrivilegeDetailVO.setFormPrivilegeUserList(
                    groupIdMap.getOrDefault(formPrivilegeDetailVO.getId(), new ArrayList<>()));
            formPrivilegeDetailVOList.add(formPrivilegeDetailVO);
        }
        return formPrivilegeDetailVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> useTemplate(String applicationId, String templateApplicationId,
                                           List<ApplicationCategoryEntity> applicationCategoryEntityList) {
        List<FormPrivilegeEntity> formPrivilegeEntityList = new ArrayList<>();
        Map<String, String> groupIdMap = new HashMap<>();
        List<TemplateFormPrivilegeVO> templateFormPrivilegeVOList =
                templateFormPrivilegeService.getByApplicationId(templateApplicationId);
        for (TemplateFormPrivilegeVO templateFormPrivilegeVO : templateFormPrivilegeVOList) {
            String guid = ObjectId.getGuid();
            groupIdMap.put(templateFormPrivilegeVO.getId(), guid);
            FormPrivilegeEntity formPrivilegeEntity =
                    AbstractFormPrivilegeConverter.INSTANCE.toEntity(templateFormPrivilegeVO);
            dealPrivilege(formPrivilegeEntity, templateFormPrivilegeVO.getDataScope());
            formPrivilegeEntity.setId(guid);
            formPrivilegeEntity.setModifierName(UserUtils.getUser().getNickName());
            formPrivilegeEntity.setCreatorName(UserUtils.getUser().getNickName());
            formPrivilegeEntity.setApplicationId(applicationId);
            formPrivilegeEntityList.add(formPrivilegeEntity);
        }
        if (CollectionUtils.isNotEmpty(formPrivilegeEntityList)) {
            saveBatch(formPrivilegeEntityList);
        }
        return groupIdMap;
    }

    @Override
    public Map<String, String> useTemplateDefaultPrivilege(String applicationId, String templateApplicationId,
                                                           List<ApplicationCategoryEntity> applicationCategoryEntityList) {
        UserDomain user = UserUtils.getUser();
        List<FormPrivilegeEntity> formPrivilegeEntityList = new ArrayList<>();
        Map<String, String> categoryToPrivilegeMap = new HashMap<>();
        for (ApplicationCategoryEntity applicationCategoryEntity : applicationCategoryEntityList) {
            FormPrivilegeEntity formPrivilegeEntity = getFormPrivilegeEntity(applicationCategoryEntity);
            if (formPrivilegeEntity == null) {
                continue;
            }
            formPrivilegeEntity.setApplicationId(applicationId);
            formPrivilegeEntity.setCategoryId(applicationCategoryEntity.getId());
            formPrivilegeEntity.setId(ObjectId.getGuid());
            formPrivilegeEntity.setCreatorName(user.getNickName());
            formPrivilegeEntity.setModifierName(user.getNickName());
            formPrivilegeEntity.setGroupName(applicationCategoryEntity.getCategoryName());
            categoryToPrivilegeMap.put(applicationCategoryEntity.getId(), formPrivilegeEntity.getId());
            formPrivilegeEntityList.add(formPrivilegeEntity);
        }
        if (CollectionUtils.isNotEmpty(formPrivilegeEntityList)) {
            saveBatch(formPrivilegeEntityList);
        }
        formPrivilegeUserService.useTemplatePrivilege(applicationId, categoryToPrivilegeMap, formPrivilegeEntityList);
        return categoryToPrivilegeMap;
    }

    @Override
    public void saveDefault(ApplicationCategoryEntity applicationCategoryEntity) {
        FormPrivilegeEntity formPrivilegeEntity = getFormPrivilegeEntity(applicationCategoryEntity);
        if (formPrivilegeEntity == null) {
            return;
        }
        formPrivilegeEntity.setCreatorName(UserUtils.getUser().getNickName());
        formPrivilegeEntity.setModifierName(UserUtils.getUser().getNickName());
        formPrivilegeEntity.setCategoryId(applicationCategoryEntity.getId());
        formPrivilegeEntity.setGroupName(applicationCategoryEntity.getCategoryName());
        formPrivilegeEntity.setApplicationId(applicationCategoryEntity.getApplicationId());
        formPrivilegeMapper.insert(formPrivilegeEntity);
        formPrivilegeUserService.saveDefault(formPrivilegeEntity.getId(), applicationCategoryEntity.getId(),
                applicationCategoryEntity.getApplicationId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealData(String applicationId) {
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormPrivilegeEntity::getApplicationId, applicationId);
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        List<String> formIdList =
                formPrivilegeEntityList.stream().map(FormPrivilegeEntity::getCategoryId).collect(Collectors.toList());
        List<FormVO> formVOList = formService.getByIdList(formIdList, applicationId);
        List<String> formTypeList = Lists.newArrayList(ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name(),
                ApplicationCategoryCategoryTypeEnum.FORM.name());
        Map<String, String> formIdToFormTypeMap =
                formVOList.stream().collect(Collectors.toMap(FormVO::getId, FormVO::getFormType));
        for (FormPrivilegeEntity formPrivilegeEntity : formPrivilegeEntityList) {
            String formType = formIdToFormTypeMap.get(formPrivilegeEntity.getCategoryId());
            if (formType != null) {
                if (formTypeList.contains(formType)) {
                    List<String> viewPrivilegeList =
                            JSONArray.parseArray(formPrivilegeEntity.getViewPrivilege(), String.class);
                    if (!viewPrivilegeList.contains("EXPORT")) {
                        viewPrivilegeList.add("EXPORT");
                        formPrivilegeEntity.setViewPrivilege(JSONArray.toJSONString(viewPrivilegeList));
                    }
                }
            }
        }
        updateBatchById(formPrivilegeEntityList);
    }

    @Override
    public void dealDataAll() {
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(new LambdaQueryWrapper<>());
        for (FormPrivilegeEntity formPrivilegeEntity : formPrivilegeEntityList) {
            List<String> viewPrivilegeList = JSONArray.parseArray(formPrivilegeEntity.getViewPrivilege(), String.class);
            if (StringUtils.isNotEmpty(formPrivilegeEntity.getViewPrivilege())) {
                if (viewPrivilegeList == null) {
                    continue;
                }
                if (!viewPrivilegeList.contains("EXPORT")) {
                    viewPrivilegeList.add("EXPORT");
                    formPrivilegeEntity.setViewPrivilege(JSONArray.toJSONString(viewPrivilegeList));
                }
                if (!viewPrivilegeList.contains("DETAIL")) {
                    viewPrivilegeList.add("DETAIL");
                    formPrivilegeEntity.setViewPrivilege(JSONArray.toJSONString(viewPrivilegeList));
                }
            }
        }
        updateBatchById(formPrivilegeEntityList);
    }

    @Override
    public Map<String, String> copy(String applicationId, String formId, String newFormId) {
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormPrivilegeEntity::getCategoryId, formId);
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        Map<String, String> privilegeMap = new HashMap<>();
        if (CollectionUtils.isEmpty(formPrivilegeEntityList)) {
            return privilegeMap;
        }
        for (FormPrivilegeEntity formPrivilegeEntity : formPrivilegeEntityList) {
            String guid = ObjectId.getGuid();
            privilegeMap.put(formPrivilegeEntity.getId(), guid);
            formPrivilegeEntity.setId(guid);
            formPrivilegeEntity.setCreatorName(UserUtils.getUser().getNickName());
            formPrivilegeEntity.setModifierName(UserUtils.getUser().getNickName());
            formPrivilegeEntity.setCreateTime(null);
            formPrivilegeEntity.setModifyTime(null);
            formPrivilegeEntity.setCategoryId(newFormId);
        }
        saveBatch(formPrivilegeEntityList);
        formPrivilegeUserService.copyUserScope(applicationId, applicationId, privilegeMap, newFormId);
        return privilegeMap;
    }

    @Override
    public void sort(FormPrivilegeSortRequest formPrivilegeSortRequest) {
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getApplicationId, formPrivilegeSortRequest.getApplicationId());
        queryWrapper.eq(FormPrivilegeEntity::getCategoryId, formPrivilegeSortRequest.getCategoryId());
        queryWrapper.in(FormPrivilegeEntity::getId, formPrivilegeSortRequest.getIdList());
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        Map<String, FormPrivilegeEntity> privilegeMap =
                formPrivilegeEntityList.stream().collect(Collectors.toMap(BaseUuidEntity::getId, c -> c));
        int sort = formPrivilegeEntityList.size();
        for (String id : formPrivilegeSortRequest.getIdList()) {
            FormPrivilegeEntity formPrivilegeEntity = privilegeMap.get(id);
            if (formPrivilegeEntity != null) {
                formPrivilegeEntity.setSort(sort);
            }
            sort--;
        }
        formPrivilegeMapper.batchUpdate(formPrivilegeSortRequest.getApplicationId(), formPrivilegeEntityList,
                formPrivilegeSortRequest.getCategoryId());
    }

    @Override
    public Map<String, String> copyApplication(String applicationId, String sourceApplicationId,
                                               List<ApplicationCategoryEntity> applicationCategoryEntityList,
                                               Boolean share) {
        if (CollectionUtils.isEmpty(applicationCategoryEntityList)) {
            return new HashMap<>();
        }
        List<String> categoryIdList =
                applicationCategoryEntityList.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
        LambdaQueryWrapper<FormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeEntity::getApplicationId, sourceApplicationId);
        queryWrapper.in(FormPrivilegeEntity::getCategoryId, categoryIdList);
        List<FormPrivilegeEntity> formPrivilegeEntityList = formPrivilegeMapper.selectList(queryWrapper);
        Map<String, String> privilegeMap = new HashMap<>();
        if (CollectionUtils.isEmpty(formPrivilegeEntityList)) {
            return privilegeMap;
        }
        for (FormPrivilegeEntity formPrivilegeEntity : formPrivilegeEntityList) {
            String guid = ObjectId.getGuid();
            privilegeMap.put(formPrivilegeEntity.getId(), guid);
            formPrivilegeEntity.setId(guid);
            formPrivilegeEntity.setCreatorName(UserUtils.getUser().getNickName());
            formPrivilegeEntity.setModifierName(UserUtils.getUser().getNickName());
            formPrivilegeEntity.setApplicationId(applicationId);
            formPrivilegeEntity.setCreateTime(null);
            formPrivilegeEntity.setModifyTime(null);
            if (share) {
                dealPrivilege(formPrivilegeEntity, formPrivilegeEntity.getDataScope());
            }
        }
        saveBatch(formPrivilegeEntityList);
        formPrivilegeUserService.copyUserScope(applicationId, sourceApplicationId, privilegeMap, null);
        return privilegeMap;
    }

    private static void dealPrivilege(FormPrivilegeEntity formPrivilegeEntity, String formPrivilegeEntity1) {
        formPrivilegeEntity.setUserPrivilege("ALL");
        if (StringUtils.isNotEmpty(formPrivilegeEntity1)) {
            List<FormPrivilegeDataScope> formPrivilegeDataScopes =
                    JSONArray.parseArray(formPrivilegeEntity1, FormPrivilegeDataScope.class);
            if (CollectionUtils.isNotEmpty(formPrivilegeDataScopes)) {
                if (formPrivilegeDataScopes.stream().map(FormPrivilegeDataScope::getDataScopeType)
                        .collect(Collectors.toList()).contains("CUSTOM_DEPT")) {
                    formPrivilegeEntity.setDataScope("[{\"dataScopeType\":\"ALL\"}]");
                }
                FormPrivilegeDataScope formPrivilegeDataScope = formPrivilegeDataScopes.stream()
                        .filter(c -> FormPrivilegeDataScopeTypeEnum.CUSTOM_FILTER.name().equals(c.getDataScopeType()))
                        .findFirst().orElse(null);
                if (formPrivilegeDataScope != null && formPrivilegeDataScope.getFilter() != null) {
                    if (CollectionUtils.isNotEmpty(formPrivilegeDataScope.getFilter().getConditionList())) {
                        formPrivilegeDataScope.getFilter().getConditionList().stream()
                                .filter(c -> MongodbSearchConditionQuoteTypeEnum.PRIVILEGE.name()
                                        .equals(c.getQuoteType())).findFirst().ifPresent(
                                        mongodbSearchCondition -> formPrivilegeEntity.setDataScope(
                                                "[{\"dataScopeType\":\"ALL\"}]"));
                    }
                }
            }
        }
    }

    private static FormPrivilegeEntity getFormPrivilegeEntity(ApplicationCategoryEntity applicationCategoryEntity) {
        FormPrivilegeEntity formPrivilegeEntity = new FormPrivilegeEntity();
        if (ApplicationCategoryCategoryTypeEnum.DASH.name().equals(applicationCategoryEntity.getCategoryType()) ||
                ApplicationCategoryCategoryTypeEnum.WEB.name().equals(applicationCategoryEntity.getCategoryType())) {
            formPrivilegeEntity = FormPrivilegeEntity.getDashboardFormDefaultConfig();
        } else if (ApplicationCategoryCategoryTypeEnum.FORM.name()
                .equals(applicationCategoryEntity.getCategoryType())) {
            formPrivilegeEntity = FormPrivilegeEntity.getFormDefaultConfig();
        } else if (ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name()
                .equals(applicationCategoryEntity.getCategoryType())) {
            formPrivilegeEntity = FormPrivilegeEntity.getFlowableFormDefaultConfig();
        } else if (ApplicationCategoryCategoryTypeEnum.VIEW.name()
                .equals(applicationCategoryEntity.getCategoryType())) {
            formPrivilegeEntity = FormPrivilegeEntity.getViewDefaultConfig();
        } else {
            return null;
        }
        return formPrivilegeEntity;
    }

}
