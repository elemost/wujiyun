package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wuji.common.api.FormDataFactoryExecuteApi;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.entity.BaseUuidEntity;
import com.wuji.common.model.vo.DataFactoryReturnFieldCommonVO;
import com.wuji.common.model.vo.DataFactoryStageInfoVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.converter.AbstractFormConfigCommonConverter;
import com.wuji.service.converter.AbstractFormConverter;
import com.wuji.service.converter.AbstractFormPublishConverter;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.enums.SystemDefaultFieldEnum;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.mapper.FormMapper;
import com.wuji.service.model.entity.FormEntity;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.request.FormCreateRequest;
import com.wuji.service.model.request.FormPublishPublishRequest;
import com.wuji.service.model.request.FormUpdateRequest;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormFieldVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormExtraFunctionTitleService;
import com.wuji.service.service.FormModuleService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormPublishService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.FormConfigUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 表单 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
@Service
public class FormServiceImpl extends ServiceImpl<FormMapper, FormEntity> implements FormService {

    @Autowired
    private FormMapper formMapper;

    @Autowired
    private FormPublishService formPublishService;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private FormModuleService formModuleService;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private FormExtraFunctionTitleService formExtraFunctionTitleService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private FormDataFactoryExecuteApi formDataFactoryExecuteApi;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Override
    public void createForm(FormCreateRequest formCreateRequest) {
        UserDomain user = UserUtils.getUser();
        FormEntity formEntity = AbstractFormConverter.INSTANCE.toEntity(formCreateRequest);
        formEntity.setCreator(user.getUserId());
        formEntity.setModifier(user.getUserId());
        formEntity.setVersion(1);
        formEntity.setCompanyId(user.getCompanyId());
        formEntity.setFormType(formCreateRequest.getFormType());
        formMapper.insert(formEntity);
    }

    @Override
    public void updateForm(FormUpdateRequest formUpdateRequest) {
        UserDomain user = UserUtils.getUser();
        FormVO exist = info(formUpdateRequest.getId(), formUpdateRequest.getApplicationId());
        FormEntity formEntity = AbstractFormConverter.INSTANCE.toEntity(formUpdateRequest);
        formEntity.setModifier(user.getUserId());
        formEntity.setApplicationId(formUpdateRequest.getApplicationId());
        if (StringUtils.isNotEmpty(formUpdateRequest.getConfig())) {
            if (exist.getVersion() == null) {
                exist.setVersion(1);
            }
            formEntity.setVersion(exist.getVersion() + 1);
        }
        LambdaQueryWrapper<FormEntity> update = new LambdaQueryWrapper<>();
        update.eq(FormEntity::getApplicationId, formUpdateRequest.getApplicationId());
        update.eq(FormEntity::getId, formUpdateRequest.getId());
        formMapper.update(formEntity, update);
        if (ApplicationCategoryCategoryTypeEnum.DASH.name().equals(exist.getFormType())) {
            formModuleService.deleteExtra(formUpdateRequest.getModuleIdList(), exist.getApplicationId(), exist.getId());
        } else if (ApplicationCategoryCategoryTypeEnum.getFormType().contains(exist.getFormType())) {
            if (StringUtils.isNotEmpty(formUpdateRequest.getConfig())) {
                formExtraFunctionTitleService.saveTitle(formUpdateRequest.getConfig(), formUpdateRequest.getId(),
                        formUpdateRequest.getApplicationId());
            }
        }
        if (StringUtils.isNotEmpty(formUpdateRequest.getConfig())) {
            FormPublishPublishRequest formPublishPublishRequest =
                    AbstractFormPublishConverter.INSTANCE.toRequest(exist);
            formPublishService.publish(formPublishPublishRequest);
        }
    }

    @Override
    public FormVO info(String id, String applicationId) {
        LambdaQueryWrapper<FormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormEntity::getId, id);
        queryWrapper.eq(FormEntity::getApplicationId, applicationId);
        FormEntity formEntity = formMapper.selectOne(queryWrapper);
        if (formEntity == null) {
            throw new ServiceException(ServiceResultCode.DATA_NOT_EXIST);
        }
        ApplicationCategoryVO applicationCategoryVO = applicationCategoryService.info(id, applicationId);
        FormVO formVO = AbstractFormConverter.INSTANCE.toVO(formEntity);
        formVO.setFormName(applicationCategoryVO.getCategoryName());
        formVO.setShowType(applicationCategoryVO.getShowType());
        formVO.setPublished(applicationCategoryVO.getPublished());
        formVO.setCompanyId(applicationCategoryVO.getCompanyId());
        formVO.setSourceId(applicationCategoryVO.getSourceId());
        ApplicationVO applicationVO = applicationService.detail(applicationId);
        if (applicationVO != null) {
            formVO.setApplicationName(applicationVO.getApplicationName());
        }
        return formVO;
    }

    @Override
    public List<FormVO> getByIdList(List<String> formIdList, String applicationId) {
        if (CollectionUtils.isEmpty(formIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormEntity::getId, formIdList);
        queryWrapper.eq(StringUtils.isNotEmpty(applicationId), FormEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormEntity::getDeleted, Boolean.FALSE);
        List<FormEntity> formEntityList = formMapper.selectList(queryWrapper);
        List<ApplicationCategoryVO> applicationCategoryVOList =
                applicationCategoryService.getByIdList(formIdList, applicationId);
        Map<String, String> categoryNameMap = applicationCategoryVOList.stream()
                .collect(Collectors.toMap(ApplicationCategoryVO::getId, ApplicationCategoryVO::getCategoryName));
        List<FormVO> formVOS = new ArrayList<>();
        for (FormEntity formEntity : formEntityList) {
            FormVO formVO = AbstractFormConverter.INSTANCE.toVO(formEntity);
            formVO.setFormName(categoryNameMap.get(formEntity.getId()));
            formVOS.add(formVO);
        }
        return formVOS;
    }

    @Override
    public List<FormVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<FormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(applicationId), FormEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormEntity::getDeleted, Boolean.FALSE);
        return formMapper.selectList(queryWrapper).stream().map(AbstractFormConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id, String applicationId) {
        UserDomain user = UserUtils.getUser();
        LambdaQueryWrapper<FormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormEntity::getId, id);
        queryWrapper.eq(FormEntity::getApplicationId, applicationId);
        FormEntity formEntity = new FormEntity();
        formEntity.setModifier(user.getUserId());
        formEntity.setDeleted(Boolean.TRUE);
        formMapper.update(formEntity, queryWrapper);
    }

    @Override
    public void publish(String id, String applicationId) {
        LambdaQueryWrapper<FormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormEntity::getId, id);
        queryWrapper.eq(FormEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormEntity::getApplicationId, applicationId);
        FormEntity formEntity = formMapper.selectOne(queryWrapper);
        if (formEntity == null) {
            throw new ServiceException(ServiceResultCode.DATA_NOT_EXIST);
        }
        FormPublishPublishRequest formPublishPublishRequest =
                AbstractFormPublishConverter.INSTANCE.toRequest(formEntity);
        formPublishService.publish(formPublishPublishRequest);
        formEntity.setVersion(formEntity.getVersion() + 1);
        formEntity.setModifier(UserUtils.getUser().getUserId());
        formMapper.update(formEntity, queryWrapper);
    }

    @Override
    public FieldExistNameVO getFormConfigCommonList(String formId, Boolean containSystem, String applicationId) {
        if (formId.startsWith(Constants.AGGREGATE_TABLE)) {
            return formAggregateService.getFields(formId, applicationId);
        } else if (formId.startsWith(Constants.FAC_PREFIX)) {
            return getFieldExistNameVO(formId, applicationId);
        } else {
            FormVO info = info(formId, applicationId);
            List<FormConfigCommon> formConfigCommonList =
                    FormConfigUtils.getConfigList(info.getConfig(), info.getFormType(), containSystem);
            FieldExistNameVO fieldExistNameVO = new FieldExistNameVO();
            fieldExistNameVO.setFields(formConfigCommonList);
            fieldExistNameVO.setName(info.getFormName());
            return fieldExistNameVO;
        }

    }

    private FieldExistNameVO getFieldExistNameVO(String formId, String applicationId) {
        DataFactoryStageInfoVO stageField = formDataFactoryExecuteApi.getStageField(applicationId, formId);
        FieldExistNameVO fieldExistNameVO = new FieldExistNameVO();
        fieldExistNameVO.setName(stageField.getFactoryName());
        fieldExistNameVO.setFormId(formId);
        List<DataFactoryReturnFieldCommonVO> fields = stageField.getFields().get(0).getFields();
        List<FormConfigCommon> formConfigCommonList =
                fields.stream().map(AbstractFormConfigCommonConverter.INSTANCE::toConfig).collect(Collectors.toList());
        fieldExistNameVO.setFields(formConfigCommonList);
        return fieldExistNameVO;
    }

    @Override
    public FieldExistNameVO getAllFormConfigCommonList(String formId, Boolean containSystem, String applicationId,
                                                       Boolean dealSubForm) {
        if (formId.startsWith(Constants.AGGREGATE_TABLE)) {
            return formAggregateService.getFields(formId, applicationId);
        } else if (formId.startsWith(Constants.FAC_PREFIX)) {
            return getFieldExistNameVO(formId, applicationId);
        } else {
            FormVO info = info(formId, applicationId);
            List<FormConfigCommon> formConfigCommonList =
                    FormConfigUtils.getConfigList(info.getConfig(), info.getFormType(), containSystem);
            List<FormConfigCommon> fields = new ArrayList<>();
            for (FormConfigCommon formConfigCommon : formConfigCommonList) {
                FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
                if (formDataService != null) {
                    formDataService.getAllConfig(formConfigCommon, fields, dealSubForm);
                } else {
                    fields.add(formConfigCommon);
                }
            }
            FieldExistNameVO fieldExistNameVO = new FieldExistNameVO();
            fieldExistNameVO.setFields(fields);
            fieldExistNameVO.setName(info.getFormName());
            return fieldExistNameVO;
        }
    }

    @Override
    public List<FieldExistNameVO> getAllFormConfigCommonList(List<String> formIdList, Boolean containSystem,
                                                             String applicationId, Boolean dealSubForm) {
        List<FieldExistNameVO> fieldExistNameVOS = new ArrayList<>();
        List<String> formIds = new ArrayList<>();
        List<String> aggregates = new ArrayList<>();
        for (String formId : formIdList) {
            if (formId.startsWith(Constants.AGGREGATE_TABLE)) {
                aggregates.add(formId);
            } else {
                formIds.add(formId);
            }
        }
        if (CollectionUtils.isNotEmpty(aggregates)) {
            List<FieldExistNameVO> aggFieldList = formAggregateService.getFieldByAggIds(aggregates, applicationId);
            fieldExistNameVOS.addAll(aggFieldList);
        }
        if (CollectionUtils.isNotEmpty(formIds)) {
            List<FormVO> infoList = getByIdList(formIds, applicationId);
            for (FormVO info : infoList) {
                List<FormConfigCommon> formConfigCommonList =
                        FormConfigUtils.getConfigList(info.getConfig(), info.getFormType(), containSystem);
                List<FormConfigCommon> fields = new ArrayList<>();
                for (FormConfigCommon formConfigCommon : formConfigCommonList) {
                    FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
                    if (formDataService != null) {
                        formDataService.getAllConfig(formConfigCommon, fields, dealSubForm);
                    } else {
                        fields.add(formConfigCommon);
                    }
                }
                FieldExistNameVO fieldExistNameVO = new FieldExistNameVO();
                fieldExistNameVO.setTableName(info.getTableName());
                fieldExistNameVO.setFields(fields);
                fieldExistNameVO.setName(info.getFormName());
                fieldExistNameVO.setFormId(info.getId());
                fieldExistNameVOS.add(fieldExistNameVO);
            }
        }
        return fieldExistNameVOS;
    }

    @Override
    public List<FieldExistNameVO> getAllFormConfigCommonList(Boolean containSystem, String applicationId,
                                                             Boolean dealSubForm) {
        List<FieldExistNameVO> fieldExistNameVOS = new ArrayList<>();
        List<FormVO> infoList = getByApplicationId(applicationId);
        for (FormVO info : infoList) {
            List<FormConfigCommon> formConfigCommonList =
                    FormConfigUtils.getConfigList(info.getConfig(), info.getFormType(), containSystem);
            List<FormConfigCommon> fields = new ArrayList<>();
            for (FormConfigCommon formConfigCommon : formConfigCommonList) {
                FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
                if (formDataService != null) {
                    formDataService.getAllConfig(formConfigCommon, fields, dealSubForm);
                } else {
                    fields.add(formConfigCommon);
                }
            }
            FieldExistNameVO fieldExistNameVO = new FieldExistNameVO();
            fieldExistNameVO.setTableName(info.getTableName());
            fieldExistNameVO.setFields(fields);
            fieldExistNameVO.setName(info.getFormName());
            fieldExistNameVO.setFormId(info.getId());
            fieldExistNameVOS.add(fieldExistNameVO);
        }
        return fieldExistNameVOS;
    }

    @Override
    public List<FormFieldVO> getAllFormFieldVO(String applicationId, List<String> formIdList,
                                               Integer defaultFieldType) {
        LambdaQueryWrapper<FormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(applicationId), FormEntity::getApplicationId, applicationId);
        queryWrapper.in(CollectionUtils.isNotEmpty(formIdList), FormEntity::getId, formIdList);
        queryWrapper.eq(FormEntity::getDeleted, Boolean.FALSE);
        List<String> formTypes = Lists.newArrayList(ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name(),
                ApplicationCategoryCategoryTypeEnum.FORM.name());
        queryWrapper.in(FormEntity::getFormType, formTypes);
        List<FormEntity> formEntityList = formMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formEntityList)) {
            return new ArrayList<>();
        }
        formIdList = formEntityList.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList =
                applicationCategoryService.getByIdList(formIdList, applicationId);
        Map<String, String> categoryNameMap = applicationCategoryVOList.stream()
                .collect(Collectors.toMap(ApplicationCategoryVO::getId, ApplicationCategoryVO::getCategoryName));
        List<FormFieldVO> formFieldVOS = new ArrayList<>();
        for (FormEntity formEntity : formEntityList) {
            FormFieldVO formFieldVO = AbstractFormConverter.INSTANCE.toFormFieldVO(formEntity);
            formFieldVO.setCategoryName(categoryNameMap.get(formFieldVO.getId()));
            List<FormConfigCommon> configList =
                    FormConfigUtils.getConfigList(formEntity.getConfig(), formEntity.getFormType(), Boolean.TRUE);
            if (defaultFieldType != null) {
                switch (defaultFieldType) {
                    case 1:
                        configList.addAll(SystemDefaultFieldEnum.dataStreamSystemField());
                        break;
                    case 2:
                        List<FormConfigCommon> defaultList = SystemDefaultFieldEnum.flowableSystemField();
                        configList.addAll(defaultList);
                        break;
                }

            }
            formFieldVO.setFields(configList);
            formFieldVOS.add(formFieldVO);
        }
        return formFieldVOS;
    }

    @Override
    public List<FieldExistNameVO> getAllFormFieldVO(List<String> applicationIds, List<String> formIdList,
                                                    Boolean needSubForm, Boolean needDeleted) {
        LambdaQueryWrapper<FormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormEntity::getApplicationId, applicationIds);
        queryWrapper.in(FormEntity::getId, formIdList);
        if (!needDeleted) {
            queryWrapper.eq(FormEntity::getDeleted, false);
        }
        List<FormEntity> formEntityList = formMapper.selectList(queryWrapper);
        List<FieldExistNameVO> fieldExistNameVOS = new ArrayList<>();
        for (FormEntity formEntity : formEntityList) {
            List<FormConfigCommon> configList =
                    FormConfigUtils.getConfigList(formEntity.getConfig(), formEntity.getFormType(), Boolean.TRUE);
            List<FormConfigCommon> fields = new ArrayList<>();
            for (FormConfigCommon formConfigCommon : configList) {
                FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
                if (formDataService != null) {
                    formDataService.getAllConfig(formConfigCommon, fields, needSubForm);
                } else {
                    fields.add(formConfigCommon);
                }
            }
            FieldExistNameVO fieldExistNameVO = new FieldExistNameVO();
            fieldExistNameVO.setTableName(formEntity.getTableName());
            fieldExistNameVO.setFields(fields);
            fieldExistNameVO.setApplicationId(formEntity.getApplicationId());
            fieldExistNameVO.setFormId(formEntity.getId());
            fieldExistNameVOS.add(fieldExistNameVO);
        }
        return fieldExistNameVOS;
    }

    @Override
    public void updateTableName(String applicationId) {
        LambdaQueryWrapper<FormEntity> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(FormEntity::getApplicationId, applicationId);
        FormEntity formEntity = new FormEntity();
        formEntity.setTableName(applicationId);
        formMapper.update(formEntity, updateWrapper);
    }

    @Override
    public Map<String, Map<String, FormConfigEncryptKey>> getEncrypt(String applicationId) {
        LambdaQueryWrapper<FormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(applicationId), FormEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormEntity::getDeleted, Boolean.FALSE);
        // queryWrapper.in(FormEntity::getFormType,
        //         Lists.newArrayList(ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name(),
        //                 ApplicationCategoryCategoryTypeEnum.FORM.name()));
        List<FormEntity> formEntityList = formMapper.selectList(queryWrapper);
        Map<String, Map<String, FormConfigEncryptKey>> encryptKeyMap = new HashMap<>();
        for (FormEntity formEntity : formEntityList) {
            if (!Lists.newArrayList(ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name(),
                    ApplicationCategoryCategoryTypeEnum.FORM.name()).contains(formEntity.getFormType())) {
                continue;
            }
            if (StringUtils.isEmpty(formEntity.getConfig())) {
                continue;
            }
            JSONObject jsonObject = JSONObject.parseObject(formEntity.getConfig());
            List<FormConfigEncryptKey> encryptKeyList =
                    JSONArray.parseArray(jsonObject.getString("encryptKey"), FormConfigEncryptKey.class);
            if (CollectionUtils.isNotEmpty(encryptKeyList)) {
                Map<String, FormConfigEncryptKey> keyMap =
                        encryptKeyList.stream().collect(Collectors.toMap(FormConfigEncryptKey::getName, c -> c));
                encryptKeyMap.put(formEntity.getId(), keyMap);
            }
        }
        return encryptKeyMap;
    }

    @Override
    public void copy(String sourceId, String formId, String applicationId) {
        UserDomain user = UserUtils.getUser();
        LambdaQueryWrapper<FormEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormEntity::getId, sourceId);
        queryWrapper.eq(FormEntity::getApplicationId, applicationId);
        FormEntity formEntity = formMapper.selectOne(queryWrapper);
        if (formEntity == null) {
            return;
        }
        LambdaQueryWrapper<FormEntity> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(FormEntity::getId, formId);
        updateWrapper.eq(FormEntity::getApplicationId, applicationId);
        formEntity.setId(formId);
        formEntity.setCreator(user.getUserId());
        formEntity.setModifier(user.getUserId());
        formMapper.update(formEntity, updateWrapper);

    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId, Boolean share, Boolean needData) {
        UserDomain user = UserUtils.getUser();
        List<FormVO> sourceFormList = getByApplicationId(sourceApplicationId);
        if (CollectionUtils.isEmpty(sourceFormList)) {
            return;
        }
        List<FormEntity> formEntityList = new ArrayList<>();
        for (FormVO sourceFormVO : sourceFormList) {
            FormEntity formEntity = AbstractFormConverter.INSTANCE.toEntity(sourceFormVO);
            formEntity.setApplicationId(applicationId);
            formEntity.setTableName(applicationId);
            formEntity.setCreator(user.getUserId());
            formEntity.setModifier(user.getUserId());
            formEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            formEntityList.add(formEntity);
            if (ApplicationCategoryCategoryTypeEnum.getFormType().contains(sourceFormVO.getFormType())) {
                if (needData) {
                    formMongoDbService.copyData(sourceFormVO.getConfig(), sourceFormVO.getTableName(),
                            formEntity.getTableName(), !share, Boolean.FALSE, sourceFormVO.getApplicationId(),
                            sourceFormVO.getId(), formEntity.getApplicationId());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(formEntityList)) {
            saveBatch(formEntityList);
        }
        formModuleService.copyApplication(applicationId, sourceApplicationId, share);
    }
}
