package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuji.common.api.FormDataFactoryExecuteApi;
import com.wuji.common.enums.RepeatTriggerEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.DataFactoryInputFormVO;
import com.wuji.common.model.vo.DataFactoryReturnFieldCommonVO;
import com.wuji.common.model.vo.DataFactoryStageCommonFieldVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.trans.MultiTransactional;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.quartz.constant.ScheduleConstants;
import com.wuji.quartz.enums.JobGroupEnum;
import com.wuji.quartz.model.request.JobRequest;
import com.wuji.quartz.service.JobService;
import com.wuji.quartz.util.CronUtils;
import com.wuji.service.context.DataStreamContext;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.converter.AbstractApplicationCategoryConverter;
import com.wuji.service.converter.AbstractApplicationConverter;
import com.wuji.service.converter.AbstractFormAggregateConverter;
import com.wuji.service.converter.AbstractFormDataFactoryConverter;
import com.wuji.service.converter.AbstractFormDataFactoryPublishConverter;
import com.wuji.service.converter.AbstractFormDataStreamConverter;
import com.wuji.service.converter.AbstractFormDataStreamPublishConverter;
import com.wuji.service.converter.AbstractFormExtraFunctionConverter;
import com.wuji.service.converter.AbstractFormExtraFunctionRelationConverter;
import com.wuji.service.converter.AbstractFormInfoConverter;
import com.wuji.service.converter.AbstractFormModuleConverter;
import com.wuji.service.converter.AbstractFormMongoDbConverter;
import com.wuji.service.converter.AbstractFormPrivilegeConverter;
import com.wuji.service.converter.AbstractFormPublicPublishConverter;
import com.wuji.service.converter.AbstractFormQuoteConverter;
import com.wuji.service.converter.AbstractFormRuleConverter;
import com.wuji.service.converter.AbstractTemplateApplicationConverter;
import com.wuji.service.converter.AbstractTemplateFormConverter;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.enums.FormDataStreamConfigTypeEnum;
import com.wuji.service.enums.FormDataStreamStateEnum;
import com.wuji.service.enums.FormExtraFunctionTypeEnum;
import com.wuji.service.mapper.FormDataFactoryMapper;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.entity.ApplicationEntity;
import com.wuji.service.model.entity.FormAggregateEntity;
import com.wuji.service.model.entity.FormDataFactoryEntity;
import com.wuji.service.model.entity.FormDataStreamEntity;
import com.wuji.service.model.entity.FormEntity;
import com.wuji.service.model.entity.FormExtraFunctionEntity;
import com.wuji.service.model.entity.FormExtraFunctionRelationEntity;
import com.wuji.service.model.entity.FormInfoEntity;
import com.wuji.service.model.entity.FormModelEntity;
import com.wuji.service.model.entity.FormModuleEntity;
import com.wuji.service.model.entity.FormPrivilegeEntity;
import com.wuji.service.model.entity.FormPublicPublishEntity;
import com.wuji.service.model.entity.FormQuoteEntity;
import com.wuji.service.model.entity.FormRuleEntity;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormFataFactorySyncConfig;
import com.wuji.service.model.info.TemplateApplicationCategoryVO;
import com.wuji.service.model.info.TemplateApplicationInfoVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamTimeTriggerNode;
import com.wuji.service.model.request.FormDataFactoryInputRequest;
import com.wuji.service.model.request.FormDataFactoryPublishRequest;
import com.wuji.service.model.request.FormDataFactorySyncConfigRequest;
import com.wuji.service.model.request.FormDataStreamPublishRequest;
import com.wuji.service.model.request.TemplateApplicationRequest;
import com.wuji.service.model.vo.TemplateApplicationVO;
import com.wuji.service.model.vo.TemplateFormAggregateVO;
import com.wuji.service.model.vo.TemplateFormDataFactoryVO;
import com.wuji.service.model.vo.TemplateFormDataStreamVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionRelationVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionVO;
import com.wuji.service.model.vo.TemplateFormInfoVO;
import com.wuji.service.model.vo.TemplateFormModuleVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeVO;
import com.wuji.service.model.vo.TemplateFormPublicPublishVO;
import com.wuji.service.model.vo.TemplateFormQuoteVO;
import com.wuji.service.model.vo.TemplateFormRuleVO;
import com.wuji.service.model.vo.TemplateFormVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.ApplicationOemService;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormDataFactoryInputService;
import com.wuji.service.service.FormDataFactoryPublishService;
import com.wuji.service.service.FormDataFactoryService;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormDataStreamPublishService;
import com.wuji.service.service.FormDataStreamService;
import com.wuji.service.service.FormExtraFunctionRelationService;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormInfoService;
import com.wuji.service.service.FormModelService;
import com.wuji.service.service.FormModuleService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormPublicPublishService;
import com.wuji.service.service.FormQuoteService;
import com.wuji.service.service.FormRuleService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.FormConfigUtils;
import com.wuji.service.utils.FormPrivilegeUtils;
import com.wuji.service.utils.TemplateDealConfigUtil;
import com.wuji.systemapi.client.user.model.FormDataRequest;
import com.wuji.systemapi.client.user.model.LowcodeDataOpenDomain;
import com.wuji.systemapi.client.user.model.TemplateApplicationOpenRequest;
import com.wuji.systemapi.service.TemplateApplicationOpenService;
import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import com.wuji.workflow.model.request.ModelRequest;
import com.wuji.workflow.model.vo.FlowableConfigVO;
import com.wuji.workflow.model.vo.ModelCopyVO;
import com.wuji.workflow.service.ModelManageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationOemServiceImpl implements ApplicationOemService {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private FormService formService;

    @Autowired
    private FormModuleService formModuleService;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Autowired
    private FormModelService formModelService;

    @Autowired
    private ModelManageService modelManageService;

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Autowired
    private FormExtraFunctionRelationService formExtraFunctionRelationService;

    @Autowired
    private FormQuoteService formQuoteService;

    @Autowired
    private FormPublicPublishService formPublicPublishService;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private FormDataStreamService formDataStreamService;

    @Autowired
    private FormInfoService formInfoService;

    @Autowired
    private FormRuleService formRuleService;

    @Autowired
    private FormDataFactoryService formDataFactoryService;

    @Autowired
    private DataStreamContext dataStreamContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FormDataStreamPublishService formDataStreamPublishService;

    @Autowired
    private JobService jobService;

    @Autowired
    private FormDataFactoryPublishService formDataFactoryPublishService;

    @Autowired
    private FormDataFactoryExecuteApi formDataFactoryExecuteApi;

    @Autowired
    private FormDataFactoryInputService formDataFactoryInputService;

    @Autowired
    private FormDataFactoryMapper formDataFactoryMapper;

    @Autowired
    private TemplateApplicationOpenService templateApplicationOpenService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @MultiTransactional(value = {"mybatisTransactionManager"})
    public String useTemplate(String templateApplicationId, Boolean needData) {
        UserDomain user = UserUtils.getUser();
        new TemplateApplicationInfoVO();
        TemplateApplicationInfoVO templateApplicationInfoVO;
        String templateInfo = templateApplicationOpenService.getTemplateInfo(templateApplicationId);
        try {
            templateApplicationInfoVO = objectMapper.readValue(templateInfo, TemplateApplicationInfoVO.class);
        } catch (Exception e) {
            log.error("解析模板失败", e);
            return null;
        }
        String applicationId = SnowFlakeIdUtils.generateStr();
        ApplicationEntity applicationEntity =
                AbstractApplicationConverter.INSTANCE.toEntity(templateApplicationInfoVO.getTemplateApplicationVO());
        applicationEntity.setId(applicationId);
        Long useTimes = applicationService.useTimes(templateApplicationId);
        if (useTimes != null && useTimes != 0) {
            useTimes = useTimes + 1;
            applicationEntity.setApplicationName(applicationEntity.getApplicationName() + "(" + useTimes + ")");
        }
        applicationEntity.setCompanyId(user.getCompanyId());
        applicationEntity.setCreator(user.getUserId());
        applicationEntity.setModifier(user.getUserId());
        applicationEntity.setCreateTime(new Date());
        applicationEntity.setTemplateId(templateApplicationId);
        applicationEntity.setModifyTime(new Date());
        applicationService.save(applicationEntity);

        List<ApplicationCategoryEntity> applicationCategoryEntityList = new ArrayList<>();
        List<TemplateApplicationCategoryVO> flowableList = new ArrayList<>();
        long time = new Date().getTime();
        for (TemplateApplicationCategoryVO templateApplicationCategoryVO : templateApplicationInfoVO.getApplicationCategoryVOS()) {
            if (ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name()
                    .equals(templateApplicationCategoryVO.getCategoryType())) {
                flowableList.add(templateApplicationCategoryVO);
            }
            ApplicationCategoryEntity applicationCategoryEntity =
                    AbstractApplicationCategoryConverter.INSTANCE.toEntity(templateApplicationCategoryVO);
            applicationCategoryEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            applicationCategoryEntity.setApplicationId(applicationId);
            applicationCategoryEntity.setModifier(user.getUserId());
            time = time + 1000;
            applicationCategoryEntity.setCreateTime(new Date(time));
            applicationCategoryEntity.setCreator(user.getUserId());
            applicationCategoryEntityList.add(applicationCategoryEntity);
        }
        applicationCategoryService.saveBatch(applicationCategoryEntityList);

        saveForm(templateApplicationInfoVO, applicationId, user, needData, templateApplicationId);
        saveFormModule(templateApplicationInfoVO, applicationId);
        Map<String, String> categoryToPrivilegeMap = savePrivilege(templateApplicationInfoVO, applicationId);
        saveModel(flowableList, templateApplicationInfoVO, applicationId);
        savePrivilege(templateApplicationInfoVO, applicationId, categoryToPrivilegeMap);
        saveFormQuote(templateApplicationInfoVO, applicationId, user);
        saveFormPublish(templateApplicationInfoVO);
        saveFormAggregate(templateApplicationInfoVO, applicationId);
        saveFormDataStream(templateApplicationInfoVO, applicationId);
        saveFormInfo(templateApplicationInfoVO, applicationId);
        saveFormRule(templateApplicationInfoVO, applicationId);
        saveDataFactory(templateApplicationInfoVO, applicationId);
        return applicationId;
    }

    @Override
    public TemplateApplicationVO info(String id) {
        String info = templateApplicationOpenService.templateInfo(id);
        return JSONObject.parseObject(info, TemplateApplicationVO.class);
    }

    @Override
    public QueryPageVO<TemplateApplicationVO> queryList(TemplateApplicationRequest templateApplicationRequest) {
        TemplateApplicationOpenRequest applicationOpenRequest =
                AbstractTemplateApplicationConverter.INSTANCE.toRequest(templateApplicationRequest);
        String templateList = templateApplicationOpenService.templateList(applicationOpenRequest);
        return JSONObject.parseObject(templateList, new TypeReference<QueryPageVO<TemplateApplicationVO>>() {
        });
    }

    private void saveForm(TemplateApplicationInfoVO templateApplicationInfoVO, String applicationId, UserDomain user,
                          Boolean needData, String templateApplicationId) {
        List<TemplateFormVO> templateFormVOList = templateApplicationInfoVO.getTemplateFormList();
        if (CollectionUtils.isEmpty(templateFormVOList)) {
            return;
        }
        List<FormEntity> formEntityList = new ArrayList<>();
        List<FormDataRequest> formDataRequests = new ArrayList<>();
        for (TemplateFormVO templateFormVO : templateFormVOList) {
            FormEntity formEntity = AbstractTemplateFormConverter.INSTANCE.toEntity(templateFormVO);
            formEntity.setApplicationId(applicationId);
            formEntity.setTableName(applicationId);
            formEntity.setCreator(user.getUserId());
            formEntity.setModifier(user.getUserId());
            formEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            formEntity.setConfig(TemplateDealConfigUtil.dealConfig(templateFormVO.getConfig()));
            formEntityList.add(formEntity);
            if (ApplicationCategoryCategoryTypeEnum.getFormType().contains(templateFormVO.getFormType())) {
                FormDataRequest formDataRequest = new FormDataRequest();
                formDataRequest.setFormId(templateFormVO.getId());
                formDataRequest.setTableName(templateFormVO.getTableName());
                formDataRequests.add(formDataRequest);
            }
        }
        if (needData) {
            List<LowcodeDataOpenDomain> lowcodeDataOpenDomains =
                    templateApplicationOpenService.queryList(templateApplicationId, formDataRequests);
            List<LowcodeDataDomain> lowcodeDataDomainList =
                    lowcodeDataOpenDomains.stream().map(AbstractFormMongoDbConverter.INSTANCE::toDomain)
                            .collect(Collectors.toList());
            FormUser current = FormUser.getCurrent(UserUtils.getUser());
            List<FormDept> currentDept = FormDept.getCurrentDept(UserUtils.getUser());
            Map<String, List<LowcodeDataDomain>> formIdToDataMap =
                    lowcodeDataDomainList.stream().collect(Collectors.groupingBy(LowcodeDataDomain::getFormId));
            for (TemplateFormVO templateFormVO : templateFormVOList) {
                if (!ApplicationCategoryCategoryTypeEnum.getFormType().contains(templateFormVO.getFormType())) {
                    continue;
                }
                List<FormConfigCommon> formConfigCommonList =
                        FormConfigUtils.getConfigList(templateFormVO.getConfig(), "", Boolean.FALSE);
                List<LowcodeDataDomain> lowcodeDataDomains = formIdToDataMap.get(templateFormVO.getId());
                if (CollectionUtils.isEmpty(lowcodeDataDomains)) {
                    continue;
                }
                for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomains) {
                    lowcodeDataDomain.setCreator(current);
                    lowcodeDataDomain.setModifier(current);
                    lowcodeDataDomain.setApplicationId(applicationId);
                    lowcodeDataDomain.setDeptList(currentDept);
                    lowcodeDataDomain.setProcessInstanceId(null);
                    lowcodeDataDomain.setParentInfo(null);
                    lowcodeDataDomain.setStatus(FormDataStatusEnum.PASS.name());
                    lowcodeDataDomain.setUuid(ObjectId.getGuid());
                    lowcodeDataDomain.setCompanyId(UserUtils.getUser().getCompanyId());
                    for (FormConfigCommon formConfigCommon : formConfigCommonList) {
                        FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
                        if (formDataService != null) {
                            formDataService.dealWhileUseTemplate(lowcodeDataDomain.getInstValue(), formConfigCommon,
                                    current, currentDept, true);
                        }
                    }
                }
                mongoTemplate.insert(lowcodeDataDomains, applicationId);
            }
        }
        if (CollectionUtils.isNotEmpty(formEntityList)) {
            formService.saveBatch(formEntityList);
        }
    }

    private void saveFormModule(TemplateApplicationInfoVO templateApplicationInfoVO, String applicationId) {
        List<TemplateFormModuleVO> templateFormModuleVOList = templateApplicationInfoVO.getFormModuleList();
        if (CollectionUtils.isEmpty(templateFormModuleVOList)) {
            return;
        }
        List<FormModuleEntity> formModuleEntityList = new ArrayList<>();
        for (TemplateFormModuleVO templateFormModuleVO : templateFormModuleVOList) {
            FormModuleEntity formModuleEntity = AbstractFormModuleConverter.INSTANCE.toEntity(templateFormModuleVO);
            formModuleEntity.setApplicationId(applicationId);
            formModuleEntity.setCreator(UserUtils.getUser().getNickName());
            formModuleEntity.setModifier(UserUtils.getUser().getNickName());
            String config = TemplateDealConfigUtil.deal(
                    templateApplicationInfoVO.getTemplateApplicationVO().getSourceApplicationId(), applicationId,
                    templateFormModuleVO.getModuleType(), templateFormModuleVO.getConfig(), applicationId);
            formModuleEntity.setConfig(config);
            formModuleEntityList.add(formModuleEntity);
        }
        formModuleService.saveBatch(formModuleEntityList);
    }

    private Map<String, String> savePrivilege(TemplateApplicationInfoVO templateApplicationInfoVO,
                                              String applicationId) {
        List<FormPrivilegeEntity> formPrivilegeEntityList = new ArrayList<>();
        Map<String, String> groupIdMap = new HashMap<>();
        List<TemplateFormPrivilegeVO> templateFormPrivilegeVOList =
                templateApplicationInfoVO.getTemplateFormPrivilegeVOS();
        for (TemplateFormPrivilegeVO templateFormPrivilegeVO : templateFormPrivilegeVOList) {
            String guid = ObjectId.getGuid();
            groupIdMap.put(templateFormPrivilegeVO.getId(), guid);
            FormPrivilegeEntity formPrivilegeEntity =
                    AbstractFormPrivilegeConverter.INSTANCE.toEntity(templateFormPrivilegeVO);
            FormPrivilegeUtils.dealPrivilege(formPrivilegeEntity, templateFormPrivilegeVO.getDataScope());
            formPrivilegeEntity.setId(guid);
            formPrivilegeEntity.setModifierName(UserUtils.getUser().getNickName());
            formPrivilegeEntity.setCreatorName(UserUtils.getUser().getNickName());
            formPrivilegeEntity.setApplicationId(applicationId);
            formPrivilegeEntityList.add(formPrivilegeEntity);
        }
        if (CollectionUtils.isNotEmpty(formPrivilegeEntityList)) {
            formPrivilegeService.saveBatch(formPrivilegeEntityList);
        }
        return groupIdMap;
    }

    private void saveModel(List<TemplateApplicationCategoryVO> flowableList,
                           TemplateApplicationInfoVO templateApplicationInfo, String applicationId) {
        List<String> formIdList =
                flowableList.stream().map(TemplateApplicationCategoryVO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(formIdList)) {
            return;
        }
        Map<String, TemplateApplicationCategoryVO> categoryIdMap =
                flowableList.stream().collect(Collectors.toMap(TemplateApplicationCategoryVO::getId, c -> c));
        List<FlowableConfigVO> flowableConfigVOS = templateApplicationInfo.getFormModels();
        if (CollectionUtils.isEmpty(flowableConfigVOS)) {
            return;
        }
        List<FormModelEntity> templateFormModelEntityList = new ArrayList<>();
        for (FlowableConfigVO flowableConfigVO : flowableConfigVOS) {
            TemplateApplicationCategoryVO applicationCategoryEntity = categoryIdMap.get(flowableConfigVO.getFormId());
            String key = "form_process_" + applicationCategoryEntity.getId() + "_" + applicationId;
            ModelCopyVO modelCopyVO = copyModel(applicationId, key, applicationCategoryEntity.getCategoryName(),
                    flowableConfigVO.getModelId(), UserUtils.getUser().getCompanyId().toString(), Boolean.FALSE,
                    flowableConfigVO.getConfig());
            if (modelCopyVO == null) {
                continue;
            }
            FormModelEntity formModelEntity = new FormModelEntity();
            formModelEntity.setModelId(modelCopyVO.getModelId());
            formModelEntity.setBusinessType(key);
            formModelEntity.setFormId(applicationCategoryEntity.getId());
            formModelEntity.setApplicationId(applicationId);
            formModelEntity.setStatus(flowableConfigVO.getStatus());
            templateFormModelEntityList.add(formModelEntity);
        }
        formModelService.saveBatch(templateFormModelEntityList);
    }

    private void savePrivilege(TemplateApplicationInfoVO templateApplicationInfoVO, String applicationId,
                               Map<String, String> categoryToPrivilegeMap) {
        List<TemplateFormExtraFunctionRelationVO> templateFormExtraFunctionRelationVOS = new ArrayList<>();
        List<TemplateFormExtraFunctionVO> templateFormExtraFunctionVOList =
                templateApplicationInfoVO.getFormExtraFunctionVOS();
        List<FormExtraFunctionEntity> formExtraFunctionEntityList = new ArrayList<>();
        Map<String, String> functionIdMap = new HashMap<>();
        for (TemplateFormExtraFunctionVO templateFormExtraFunctionVO : templateFormExtraFunctionVOList) {
            FormExtraFunctionEntity formExtraFunctionEntity =
                    AbstractFormExtraFunctionConverter.INSTANCE.toEntity(templateFormExtraFunctionVO);
            String guid = ObjectId.getGuid();
            functionIdMap.put(templateFormExtraFunctionVO.getId(), guid);
            formExtraFunctionEntity.setId(guid);
            formExtraFunctionEntity.setApplicationId(applicationId);
            formExtraFunctionEntity.setCreator(UserUtils.getUser().getNickName());
            formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
            if (FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name().equals(templateFormExtraFunctionVO.getFunctionType())) {
                formExtraFunctionEntity.setConfig(
                        TemplateDealConfigUtil.dealFunctionConfig(formExtraFunctionEntity.getConfig()));
            }
            if (CollectionUtils.isNotEmpty(templateFormExtraFunctionVO.getFormExtraFunctionRelationList())) {
                templateFormExtraFunctionRelationVOS.addAll(
                        templateFormExtraFunctionVO.getFormExtraFunctionRelationList());
            }
            formExtraFunctionEntityList.add(formExtraFunctionEntity);
        }
        TemplateDealConfigUtil.buildConfig(formExtraFunctionEntityList, functionIdMap, new HashMap<>());
        formExtraFunctionServiceImpl.saveBatch(formExtraFunctionEntityList);

        List<String> functionIdList = new ArrayList<>(functionIdMap.keySet());
        if (CollectionUtils.isEmpty(functionIdList)) {
            return;
        }
        List<FormExtraFunctionRelationEntity> formExtraFunctionRelationEntityList = new ArrayList<>();
        for (TemplateFormExtraFunctionRelationVO templateFormExtraFunctionRelationVO : templateFormExtraFunctionRelationVOS) {
            FormExtraFunctionRelationEntity formExtraFunctionRelationEntity =
                    AbstractFormExtraFunctionRelationConverter.INSTANCE.toEntity(templateFormExtraFunctionRelationVO);
            formExtraFunctionRelationEntity.setFunctionId(
                    functionIdMap.get(formExtraFunctionRelationEntity.getFunctionId()));
            formExtraFunctionRelationEntity.setBusinessId(
                    categoryToPrivilegeMap.get(formExtraFunctionRelationEntity.getBusinessId()));
            formExtraFunctionRelationEntityList.add(formExtraFunctionRelationEntity);
        }
        if (CollectionUtils.isNotEmpty(formExtraFunctionRelationEntityList)) {
            formExtraFunctionRelationService.saveBatch(formExtraFunctionRelationEntityList);
        }
    }

    private void saveFormQuote(TemplateApplicationInfoVO templateApplicationInfoVO, String applicationId,
                               UserDomain user) {
        List<TemplateFormQuoteVO> templateFormQuoteVOList = templateApplicationInfoVO.getFormQuoteList();
        if (CollectionUtils.isEmpty(templateFormQuoteVOList)) {
            return;
        }
        List<FormQuoteEntity> formQuoteEntityList = new ArrayList<>();
        for (TemplateFormQuoteVO templateFormQuoteVO : templateFormQuoteVOList) {
            FormQuoteEntity formQuoteEntity = AbstractFormQuoteConverter.INSTANCE.toEntity(templateFormQuoteVO);
            formQuoteEntity.setApplicationId(applicationId);
            formQuoteEntity.setCreatorName(user.getNickName());
            formQuoteEntity.setModifierName(user.getNickName());
            formQuoteEntityList.add(formQuoteEntity);
        }
        formQuoteService.saveBatch(formQuoteEntityList);
    }

    private void saveFormPublish(TemplateApplicationInfoVO templateApplicationInfoVO) {
        List<TemplateFormPublicPublishVO> templateFormPublicPublishVOList =
                templateApplicationInfoVO.getFormPublicPublishList();
        if (CollectionUtils.isEmpty(templateFormPublicPublishVOList)) {
            return;
        }
        List<FormPublicPublishEntity> formPublicPublishEntityList = new ArrayList<>();
        for (TemplateFormPublicPublishVO templateFormPublicPublishVO : templateFormPublicPublishVOList) {
            FormPublicPublishEntity formPublicPublishEntity =
                    AbstractFormPublicPublishConverter.INSTANCE.toEntity(templateFormPublicPublishVO);
            formPublicPublishEntity.setId(ObjectId.getGuid());
            formPublicPublishEntity.setAccessToken(
                    Objects.requireNonNull(AESUtils.encrypt(SnowFlakeIdUtils.generateStr())).replaceAll("\\+", " "));
            formPublicPublishEntity.setCreatorName(UserUtils.getUser().getNickName());
            formPublicPublishEntity.setModifierName(UserUtils.getUser().getNickName());
            formPublicPublishEntityList.add(formPublicPublishEntity);
        }
        formPublicPublishService.saveBatch(formPublicPublishEntityList);
    }

    private void saveFormAggregate(TemplateApplicationInfoVO templateApplicationInfoVO, String applicationId) {
        List<TemplateFormAggregateVO> templateFormAggregateVOList = templateApplicationInfoVO.getFormAggregateList();
        if (CollectionUtils.isEmpty(templateFormAggregateVOList)) {
            return;
        }
        List<FormAggregateEntity> formAggregateEntities = new ArrayList<>();
        for (TemplateFormAggregateVO formAggregateVO : templateFormAggregateVOList) {
            FormAggregateEntity formAggregateEntity = AbstractFormAggregateConverter.INSTANCE.toEntity(formAggregateVO);
            formAggregateEntity.setConfig(
                    TemplateDealConfigUtil.dealAggregateConfig(JSONObject.parseObject(formAggregateVO.getConfig()),
                            UserUtils.getUser()));
            formAggregateEntity.setModifier(UserUtils.getUser().getNickName());
            formAggregateEntity.setCreator(UserUtils.getUser().getNickName());
            formAggregateEntity.setApplicationId(applicationId);
            formAggregateEntities.add(formAggregateEntity);
        }
        formAggregateService.saveBatch(formAggregateEntities);
    }

    private void saveFormDataStream(TemplateApplicationInfoVO templateApplicationInfoVO, String applicationId) {
        List<TemplateFormDataStreamVO> templateFormDataStreamVOS = templateApplicationInfoVO.getFormDataStreamList();
        if (CollectionUtils.isEmpty(templateFormDataStreamVOS)) {
            return;
        }
        List<FormDataStreamEntity> formDataStreamEntityList = new ArrayList<>();
        List<FormDataStreamPublishRequest> formDataStreamPublishRequestList = new ArrayList<>();
        for (TemplateFormDataStreamVO templateFormDataStreamVO : templateFormDataStreamVOS) {
            FormDataStreamEntity formDataStreamEntity =
                    AbstractFormDataStreamConverter.INSTANCE.toEntity(templateFormDataStreamVO);
            formDataStreamEntity.setState(FormDataStreamStateEnum.PUBLISH.name());
            overWriteData(formDataStreamEntity, applicationId);
            formDataStreamEntity.setCreator(UserUtils.getUser().getNickName());
            formDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
            formDataStreamEntity.setApplicationId(applicationId);
            formDataStreamEntity.setVersion(1);
            formDataStreamEntityList.add(formDataStreamEntity);
            FormDataStreamPublishRequest formDataStreamPublishRequest =
                    AbstractFormDataStreamPublishConverter.INSTANCE.toRequest(formDataStreamEntity);
            formDataStreamPublishRequestList.add(formDataStreamPublishRequest);
        }
        formDataStreamService.saveBatch(formDataStreamEntityList);
        formDataStreamPublishService.batchPublish(formDataStreamPublishRequestList);
        for (FormDataStreamEntity formDataStreamEntity : formDataStreamEntityList) {
            timeTrigger(formDataStreamEntity, formDataStreamEntity);
        }
    }

    private void saveFormInfo(TemplateApplicationInfoVO templateApplicationInfoVO, String applicationId) {
        List<TemplateFormInfoVO> templateFormInfoVOList = templateApplicationInfoVO.getFormInfoList();
        if (CollectionUtils.isEmpty(templateFormInfoVOList)) {
            return;
        }
        List<FormInfoEntity> formInfoEntities = new ArrayList<>();
        for (TemplateFormInfoVO templateFormInfoVO : templateFormInfoVOList) {
            FormInfoEntity formInfoEntity = AbstractFormInfoConverter.INSTANCE.toEntity(templateFormInfoVO);
            formInfoEntity.setApplicationId(applicationId);
            formInfoEntity.setCreatorName(UserUtils.getUser().getNickName());
            formInfoEntity.setModifierName(UserUtils.getUser().getNickName());
            formInfoEntities.add(formInfoEntity);
        }
        formInfoService.saveBatch(formInfoEntities);
    }

    private void saveFormRule(TemplateApplicationInfoVO templateApplicationInfoVO, String applicationId) {
        List<TemplateFormRuleVO> templateFormRuleVOList = templateApplicationInfoVO.getFormRuleList();
        if (CollectionUtils.isEmpty(templateFormRuleVOList)) {
            return;
        }
        List<FormRuleEntity> formRuleEntityList = new ArrayList<>();
        for (TemplateFormRuleVO templateFormRuleVO : templateFormRuleVOList) {
            FormRuleEntity formRuleEntity = AbstractFormRuleConverter.INSTANCE.toEntity(templateFormRuleVO);
            formRuleEntity.setApplicationId(applicationId);
            formRuleEntity.setCreator(UserUtils.getUser().getNickName());
            formRuleEntity.setModifier(UserUtils.getUser().getNickName());
            formRuleEntityList.add(formRuleEntity);
        }
        formRuleService.saveBatch(formRuleEntityList);
    }

    private void saveDataFactory(TemplateApplicationInfoVO templateApplicationInfoVO, String applicationId) {
        List<TemplateFormDataFactoryVO> templateFormDataFactoryVOList =
                templateApplicationInfoVO.getFormDataFactoryList();
        if (CollectionUtils.isEmpty(templateFormDataFactoryVOList)) {
            return;
        }
        List<FormDataFactoryEntity> formDataFactoryEntityList = new ArrayList<>();
        List<FormDataFactoryPublishRequest> formDataFactoryPublishRequests = new ArrayList<>();
        for (TemplateFormDataFactoryVO templateFormDataFactoryVO : templateFormDataFactoryVOList) {
            FormDataFactoryEntity formDataFactoryEntity =
                    AbstractFormDataFactoryConverter.INSTANCE.toEntity(templateFormDataFactoryVO);
            formDataFactoryEntity.setApplicationId(applicationId);
            formDataFactoryEntity.setModifier(UserUtils.getUser().getNickName());
            formDataFactoryEntity.setCreator(UserUtils.getUser().getNickName());
            formDataFactoryEntityList.add(formDataFactoryEntity);
            FormDataFactoryPublishRequest formDataFactoryPublishRequest =
                    AbstractFormDataFactoryPublishConverter.INSTANCE.toRequest(formDataFactoryEntity);
            formDataFactoryPublishRequests.add(formDataFactoryPublishRequest);
        }
        formDataFactoryService.saveBatch(formDataFactoryEntityList);
        formDataFactoryPublishService.batchPublish(formDataFactoryPublishRequests);
        saveInput(applicationId, null);
        for (FormDataFactoryEntity formDataFactoryEntity : formDataFactoryEntityList) {
            if (formDataFactoryEntity.getSyncConfig() == null) {
                continue;
            }
            FormDataFactorySyncConfigRequest formDataFactorySyncConfigRequest = new FormDataFactorySyncConfigRequest();
            formDataFactorySyncConfigRequest.setId(formDataFactoryEntity.getId());
            formDataFactorySyncConfigRequest.setApplicationId(formDataFactoryEntity.getApplicationId());
            FormFataFactorySyncConfig formFataFactorySyncConfig =
                    JSONObject.parseObject(formDataFactoryEntity.getSyncConfig(), FormFataFactorySyncConfig.class);
            formDataFactorySyncConfigRequest.setSyncConfig(formFataFactorySyncConfig);
            syncFormConfig(formDataFactorySyncConfigRequest);
        }
    }

    private ModelCopyVO copyModel(String templateApplicationId, String key, String categoryName, String modelId,
                                  String tenantId, Boolean exist, FormModelDesignerDomain config) {
        ModelRequest modelRequest = new ModelRequest();
        modelRequest.setKey(key);
        modelRequest.setName(categoryName + "流程模型");
        modelRequest.setApplicationId(templateApplicationId);
        modelRequest.setTenantId(tenantId);
        modelRequest.setModelId(modelId);
        modelRequest.setConfig(config);
        return modelManageService.createAndPublishModel(modelRequest, true, exist);
    }

    private void overWriteData(FormDataStreamEntity formDataStreamEntity, String applicationId) {
        try {
            DataStreamCommon dataStreamCommon =
                    objectMapper.readValue(formDataStreamEntity.getConfig(), DataStreamCommon.class);
            dataStreamContext.getHandler(dataStreamCommon.getType())
                    .useTemplate(dataStreamCommon, applicationId, formDataStreamEntity.getApplicationId());
            String config = objectMapper.writer().writeValueAsString(dataStreamCommon);
            formDataStreamEntity.setConfig(config);
        } catch (Exception e) {
            log.error("转化智能助手失败", e);
        }
    }

    private void timeTrigger(FormDataStreamEntity exist, FormDataStreamEntity formDataStreamEntity) {
        if (FormDataStreamConfigTypeEnum.TIME.name().equals(exist.getConfigType())) {
            insertJob(exist, formDataStreamEntity);
        }
    }

    private void saveInput(String applicationId, List<String> ids) {
        List<DataFactoryInputFormVO> inputList = formDataFactoryExecuteApi.getAllInput(applicationId, ids);
        List<FormDataFactoryInputRequest> factoryInputs = new ArrayList<>();
        for (DataFactoryInputFormVO dataFactoryInputFormVO : inputList) {
            List<DataFactoryInputFormVO.Input> inputs = dataFactoryInputFormVO.getInputs();
            for (DataFactoryInputFormVO.Input input : inputs) {
                FormDataFactoryInputRequest formDataFactoryInputRequest = new FormDataFactoryInputRequest();
                formDataFactoryInputRequest.setDataFactoryId(dataFactoryInputFormVO.getId());
                formDataFactoryInputRequest.setApplicationId(applicationId);
                formDataFactoryInputRequest.setVersion(dataFactoryInputFormVO.getVersion());
                formDataFactoryInputRequest.setInputApplicationId(input.getApplicationId());
                formDataFactoryInputRequest.setInputFormId(input.getFormId());
                factoryInputs.add(formDataFactoryInputRequest);
            }
        }
        formDataFactoryInputService.saveBatch(factoryInputs);
    }

    public void syncFormConfig(FormDataFactorySyncConfigRequest formDataFactorySyncConfigRequest) {
        LambdaQueryWrapper<FormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryEntity::getId, formDataFactorySyncConfigRequest.getId());
        queryWrapper.eq(FormDataFactoryEntity::getApplicationId, formDataFactorySyncConfigRequest.getApplicationId());
        FormDataFactoryEntity formDataFactoryEntity = formDataFactoryMapper.selectOne(queryWrapper);
        FormFataFactorySyncConfig syncConfig = formDataFactorySyncConfigRequest.getSyncConfig();
        formDataFactoryEntity.setSyncForm(syncConfig.getEnable());
        String id = formDataFactoryEntity.getId() + "_" + formDataFactoryEntity.getApplicationId();
        formDataFactoryEntity.setSyncConfig(JSONObject.toJSONString(syncConfig));
        if (formDataFactoryEntity.getSyncForm()) {
            jobService.deleteJob(id, JobGroupEnum.DATA_FACTORY.name(), Boolean.TRUE);
            insertJob(formDataFactoryEntity, syncConfig, id);
            if (checkSyncFieldChange(formDataFactorySyncConfigRequest.getId(),
                    formDataFactorySyncConfigRequest.getApplicationId(), formDataFactoryEntity)) {
                formDataFactoryEntity.setSyncConfigError(Boolean.TRUE);
            } else {
                formDataFactoryEntity.setSyncConfigError(Boolean.FALSE);
            }
        } else {
            jobService.deleteJob(id, JobGroupEnum.DATA_FACTORY.name(), Boolean.TRUE);
        }
        formDataFactoryMapper.update(formDataFactoryEntity, queryWrapper);
    }

    private void insertJob(FormDataStreamEntity exist, FormDataStreamEntity formDataStreamEntity) {
        String businessId = exist.getId() + "_" + formDataStreamEntity.getApplicationId();
        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobName(exist.getName() + "_" + formDataStreamEntity.getId());
        jobRequest.setBusinessId(businessId);
        jobRequest.setJobGroup(JobGroupEnum.DATA_STREAM.name());
        jobRequest.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        String param = "'%s','%s'";
        jobRequest.setParam(String.format(param, exist.getId(), exist.getApplicationId()));
        try {
            DataStreamCommon dataStreamCommon =
                    objectMapper.readValue(formDataStreamEntity.getConfig(), DataStreamCommon.class);
            DataStreamTimeTriggerNode dataStreamTimeTriggerNode = (DataStreamTimeTriggerNode) dataStreamCommon;
            if ("custom".equals(dataStreamTimeTriggerNode.getTriggerTimeType())) {
                jobRequest.setCronExpression(
                        CronUtils.trans(RepeatTriggerEnum.valueOf(dataStreamTimeTriggerNode.getRepeatTrigger()),
                                dataStreamTimeTriggerNode.getCustomTime(), dataStreamTimeTriggerNode.getCustomCron()));
                jobRequest.setStartTime(dataStreamTimeTriggerNode.getCustomTime());
            } else {
                Date dateByDaytime = TimeUtils.getDateByDaytime(dataStreamTimeTriggerNode.getDaytime());
                jobRequest.setCronExpression(CronUtils.trans(RepeatTriggerEnum.DAILY, dateByDaytime,
                        dataStreamTimeTriggerNode.getCustomCron()));
            }
            jobRequest.setEndTime(dataStreamTimeTriggerNode.getEndTime());
        } catch (Exception e) {
            log.error("转化智能助手失败", e);
        }
        jobRequest.setConcurrent("0");
        jobRequest.setMisfirePolicy("3");
        String invokeTarget = "dataStreamSchedule.trigger(" + jobRequest.getParam() + ")";
        jobRequest.setInvokeTarget(invokeTarget);
        jobService.deleteJob(businessId, JobGroupEnum.DATA_STREAM.name(), Boolean.TRUE);
        jobService.insertJob(jobRequest);
    }

    private void insertJob(FormDataFactoryEntity formDataFactoryEntity, FormFataFactorySyncConfig syncConfig,
                           String id) {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobName(formDataFactoryEntity.getFactoryName() + "_" + id);
        jobRequest.setBusinessId(id);
        jobRequest.setJobGroup(JobGroupEnum.DATA_FACTORY.name());
        jobRequest.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        jobRequest.setStartTime(syncConfig.getStartTime());
        String param = "'%s','%s'";
        jobRequest.setParam(
                String.format(param, formDataFactoryEntity.getId(), formDataFactoryEntity.getApplicationId()));
        jobRequest.setCronExpression(
                CronUtils.trans(RepeatTriggerEnum.valueOf(syncConfig.getRepeatTrigger()), syncConfig.getStartTime(),
                        syncConfig.getCustomCron()));
        jobRequest.setConcurrent("0");
        jobRequest.setMisfirePolicy("3");
        String invokeTarget = "formDataFactorySchedule.trigger(" + jobRequest.getParam() + ")";
        jobRequest.setInvokeTarget(invokeTarget);
        jobService.insertJob(jobRequest);
    }

    private Boolean checkSyncFieldChange(String id, String applicationId, FormDataFactoryEntity exist) {
        if (StringUtils.isNotEmpty(exist.getSyncConfig())) {
            List<DataFactoryStageCommonFieldVO> dataFactoryExecuteApiStageField =
                    formDataFactoryExecuteApi.getStageField(applicationId, id).getFields();
            FormFataFactorySyncConfig formFataFactorySyncConfig =
                    JSONObject.parseObject(exist.getSyncConfig(), FormFataFactorySyncConfig.class);
            DataFactoryStageCommonFieldVO dataFactoryStageCommonFieldVO = dataFactoryExecuteApiStageField.get(0);
            List<DataFactoryReturnFieldCommonVO> fields = dataFactoryStageCommonFieldVO.getFields();
            List<FormFataFactorySyncConfig.MappingField> mappingFieldList =
                    formFataFactorySyncConfig.getMappingFields();
            List<String> mappingFields =
                    fields.stream().map(DataFactoryReturnFieldCommonVO::getAliasName).collect(Collectors.toList());
            for (FormFataFactorySyncConfig.MappingField mappingField : mappingFieldList) {
                if (!mappingFields.contains(mappingField.getFieldId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
