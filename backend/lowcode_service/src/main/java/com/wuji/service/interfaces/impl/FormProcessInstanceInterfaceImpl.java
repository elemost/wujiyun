package com.wuji.service.interfaces.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.cache.ConfigCache;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.enums.InMailMessageTypeEnum;
import com.wuji.common.enums.SuiteApplicationEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.info.DingTalkConfig;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.request.MessageInsertRequest;
import com.wuji.common.model.vo.ConfigVO;
import com.wuji.common.service.ConfigService;
import com.wuji.common.service.MessageCommonService;
import com.wuji.common.utils.StringUtil;
import com.wuji.common.utils.UserUtils;
import com.wuji.message.context.MessageContext;
import com.wuji.message.model.request.SendMessageRequest;
import com.wuji.message.service.EmailService;
import com.wuji.message.utils.MessageTemplateUtils;
import com.wuji.plugin.model.info.Markdown;
import com.wuji.plugin.utils.FormMarkdownUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.converter.AbstractFormWorkflowConverter;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.SystemDefaultFieldEnum;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.domain.LowcodeUpdateStateDomain;
import com.wuji.service.model.domain.ParentInfoMongodbDomain;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.FormMessageMarkdown;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.model.request.FlowableSendMessageRequest;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormDataStreamPublishService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.MongoDbService;
import com.wuji.service.utils.MessageUtils;
import com.wuji.service.utils.MongoDbDataTransUtils;
import com.wuji.service.utils.MongoSearchUtils;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.enums.ProcessStateEnum;
import com.wuji.workflow.interfaces.ProcessInstanceInterface;
import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
import com.wuji.workflow.model.info.FlowableMongodbSearchFilter;
import com.wuji.workflow.model.info.FlowableRemindConfig;
import com.wuji.workflow.model.info.FlowableSubFlowConfig;
import com.wuji.workflow.model.request.FlowableAuditRequest;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.FlowableActivityConfigService;
import com.wuji.workflow.service.ModelManageService;
import com.wuji.workflow.service.WorkFlowService;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service(Constants.VARIABLE_FORM_SERVICE)
public class FormProcessInstanceInterfaceImpl implements ProcessInstanceInterface {

    @Autowired
    private FormService formService;

    @Autowired
    private MongoDbService mongoDbService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FlowableActivityConfigService flowableActivityConfigService;

    @Autowired
    private ModelManageService modelManageService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private FormDataStreamPublishService formDataStreamPublishService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MessageCommonService messageService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ThreadPoolExecutor messageExecutor;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MessageContext messageContext;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private AdminCommonService adminCommonService;

    @Override
    public void processEnd(HistoricProcessInstance historicProcessInstance) {
        Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
        String status = processVariables.getOrDefault(FlowableConstant.PROCESS_STATUS_SING_KEY, "").toString();
        String dataUuid = processVariables.get(Constants.VARIABLE_DATA_UUID).toString();
        String formId = processVariables.get(Constants.VARIABLE_FORM_ID).toString();
        String applicationId = processVariables.get(Constants.VARIABLE_APPLICATION_ID).toString();
        FormVO info = formService.info(formId, applicationId);
        LowcodeUpdateStateDomain lowcodeUpdateStateDomain = getLowcodeUpdateStateDomain(dataUuid, info, status);
        LowcodeDataDomain lowcodeDataDomain = formMongoDbService.info(dataUuid, info.getId(), info.getApplicationId());
        if (ProcessStateEnum.TERMINATED.getStatus().equals(status)) {
            // 主流程终止 终止子流程
            workFlowService.terminateByParent(historicProcessInstance.getId());
            dataTransAndCallBackWhileComplete(lowcodeDataDomain, processVariables);
        } else {
            // 子流程完成 将子流程数据转化到主流程中 并通知主流程
            dataTransAndCallBackWhileComplete(lowcodeDataDomain, processVariables);
        }
        mongoDbService.updateData(lowcodeUpdateStateDomain);
        LowcodeDataDomain finishDomain = formMongoDbService.info(dataUuid, info.getId(), info.getApplicationId());
        FormDataStreamTrigger formDataStreamTrigger = new FormDataStreamTrigger();
        formDataStreamTrigger.setDelay(300);
        formDataStreamPublishService.trigger(formId, "process_finish", finishDomain, applicationId,
                formDataStreamTrigger);
        ModelVO modelVO =
                modelManageService.infoByProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());
        FlowableRemindConfig remindConfig = flowableActivityConfigService.getRemindConfig("end", modelVO.getModelId());
        if (remindConfig != null) {
            CompanyVO companyVO = companyService.info(UserUtils.getUser().getCompanyId());
            FlowableSendMessageRequest flowableSendMessageRequest = new FlowableSendMessageRequest();
            flowableSendMessageRequest.setName("流程结束");
            flowableSendMessageRequest.setAssigneeList(
                    Collections.singletonList(Long.valueOf(historicProcessInstance.getStartUserId())));
            String auditUrl = getAuditUrl("end", lowcodeDataDomain);
            flowableSendMessageRequest.setAuditUrl(auditUrl);
            if (remindConfig.getSendWeCom()) {
                List<FormMessageMarkdown> markdownList =
                        getMessageMarkdown(ConfigEnum.LOWCODE_WECOM_PROCESS_END.name());
                flowableSendMessageRequest.setMarkdownList(markdownList);
                messageExecutor.execute(() -> sendWecomMessage(flowableSendMessageRequest, lowcodeDataDomain, info));
            }
            if (remindConfig.getSendDingTalk()) {
                messageExecutor.execute(() -> sendDingTalkMessage(companyVO, lowcodeDataDomain, remindConfig,
                        flowableSendMessageRequest));
            }
        }
    }

    private void sendDingTalkMessage(CompanyVO companyVO, LowcodeDataDomain lowcodeDataDomain,
                                     FlowableRemindConfig remindConfig,
                                     FlowableSendMessageRequest flowableSendMessage) {
        DingTalkConfig dingTalkConfig = JSONObject.parseObject(companyVO.getPullConfig(), DingTalkConfig.class);
        String domainName = ConfigCache.getValue(ConfigEnum.LOWCODE_PUBLIC_PUBLISH_URL.name());
        String finalUrl = Constants.getDingTalkUrl(domainName, dingTalkConfig, flowableSendMessage.getAuditUrl());
        String message = getMessage(finalUrl, lowcodeDataDomain, remindConfig);
        flowableSendMessage.setAuditUrl(message);
        flowableSendMessage.setRobotCode(dingTalkConfig.getRobotCode());
        sendDingTalkMessage(flowableSendMessage, remindConfig);
    }

    private void sendDingTalkMessage(FlowableSendMessageRequest flowableSendMessage,
                                     FlowableRemindConfig remindConfig) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setUserIdList(flowableSendMessage.getAssigneeList());
        sendMessageRequest.setMessageType("sampleMarkdown");
        sendMessageRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", remindConfig.getTitle());
        jsonObject.put("text", flowableSendMessage.getAuditUrl());
        sendMessageRequest.setMessage(JSONObject.toJSONString(jsonObject));
        sendMessageRequest.setRobotId(flowableSendMessage.getRobotCode());
        messageContext.getHandler("DING_TALK").sendMessage(sendMessageRequest);
    }

    private String getMessage(String finalUrl, LowcodeDataDomain lowcodeDataDomain, FlowableRemindConfig remindConfig) {
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        Map<String, Object> context = new HashMap<>();
        Map<Long, DataStreamCalculateVO> nodeIdMap = new HashMap<>();
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setJsonValue(FormSystemFieldEnum.putSystemValue(lowcodeDataDomain));
        nodeIdMap.put(1L, dataStreamCalculateVO);
        remindConfig.getQuoteFieldJson().forEach((key, val) -> {
            DataStreamQuoteField dataStreamQuoteField =
                    JSONObject.parseObject(JSONObject.toJSONString(val), DataStreamQuoteField.class);
            dataStreamQuoteField.setNodeId(1L);
            if (SystemDefaultFieldEnum.urlKeyList().contains(dataStreamQuoteField.getQuoteFieldId())) {
                context.put(key, finalUrl);
            } else {
                List<Object> values = MongoSearchUtils.getJsonValueByQuote(nodeIdMap, dataStreamQuoteField);
                FormDataService formDataService = formDataContext.getHandler(dataStreamQuoteField.getQuoteFieldType());
                String content = null;
                if (formDataService != null) {
                    content = formDataService.transValue(values, dataStreamQuoteField.getQuoteFieldId(),
                            dataStreamQuoteField.getQuoteFieldType(), systemAllData);
                } else {
                    if (CollectionUtils.isNotEmpty(values) && values.get(0) != null) {
                        content = values.get(0).toString();
                    }
                }
                context.put(key, content);
            }
        });
        return StringUtil.replaceValue(remindConfig.getContent(), context);
    }

    @Override
    public void processStart(HistoricProcessInstance historicProcessInstance) {
        Map<String, Object> variables = historicProcessInstance.getProcessVariables();
        updateState(variables, FormDataStatusEnum.APPROVING, historicProcessInstance.getId());
    }

    @Override
    public void subFlowTaskStart(DelegateTask delegateTask) {
        ModelVO modelVO = modelManageService.infoByProcessDefinitionId(delegateTask.getProcessDefinitionId());
        if (modelVO == null) {
            return;
        }
        Map<String, Object> variables = delegateTask.getVariables();
        String applicationId = variables.getOrDefault(Constants.VARIABLE_APPLICATION_ID, "").toString();
        FlowableSubFlowConfig flowableSubConfig =
                flowableActivityConfigService.getFlowableSubConfig(delegateTask.getTaskDefinitionKey(),
                        modelVO.getModelId());
        String subFormId = flowableSubConfig.getSubFormId();

        FormVO info = formService.info(subFormId, applicationId);
        if (info == null) {
            return;
        }
        String dataUuid = variables.getOrDefault(Constants.VARIABLE_DATA_UUID, "").toString();
        String formId = variables.getOrDefault(Constants.VARIABLE_FORM_ID, "").toString();
        LowcodeDataDomain lowcodeDataDomain = formMongoDbService.info(dataUuid, formId, applicationId);
        JSONObject targetJson =
                MongoDbDataTransUtils.dataTrans(lowcodeDataDomain, flowableSubConfig.getParentFlowableDataTrans(),
                        null);
        FormInsertDataRequest formInsertDataRequest = new FormInsertDataRequest();
        formInsertDataRequest.setInstValue(targetJson);
        formInsertDataRequest.setFormId(subFormId);
        formInsertDataRequest.setVersion(info.getVersion());
        formInsertDataRequest.setStatus(FormDataStatusEnum.PASS.name());
        formInsertDataRequest.setApplicationId(lowcodeDataDomain.getApplicationId());

        ParentInfoMongodbDomain parentInfoMongodbDomain = new ParentInfoMongodbDomain();
        parentInfoMongodbDomain.setParentTaskId(delegateTask.getId());
        parentInfoMongodbDomain.setParentProcessInstanceId(delegateTask.getProcessInstanceId());
        parentInfoMongodbDomain.setCallBack(Boolean.TRUE);
        parentInfoMongodbDomain.setParentFormId(formId);
        parentInfoMongodbDomain.setParentActivityId(delegateTask.getTaskDefinitionKey());
        parentInfoMongodbDomain.setParentModelId(modelVO.getModelId());
        parentInfoMongodbDomain.setParentDataUuid(dataUuid);
        parentInfoMongodbDomain.setAssigneeId(delegateTask.getAssignee());
        if (lowcodeDataDomain.getCreator() != null) {
            FormUser formUser =
                    JSONObject.parseObject(JSONObject.toJSONString(lowcodeDataDomain.getCreator()), FormUser.class);
            parentInfoMongodbDomain.setParentAssigneeId(formUser.getAssigneeId().toString());
        }
        formInsertDataRequest.setParentInfo(parentInfoMongodbDomain);
        UserDomain user = UserUtils.getUser();
        String jsonString = JSONObject.toJSONString(user);
        formMongoDbService.insertData(formInsertDataRequest);
        UserDomain userDomain = JSONObject.parseObject(jsonString, UserDomain.class);
        UserUtils.setUser(userDomain);
    }

    @Override
    public void callBackWhileTaskCreate(Task task) {
        Map<String, Object> processVariables = task.getProcessVariables();
        String dataUuid = processVariables.getOrDefault(Constants.VARIABLE_DATA_UUID, "").toString();
        LowcodeUpdateStateDomain lowcodeUpdateStateDomain = new LowcodeUpdateStateDomain();
        lowcodeUpdateStateDomain.setUuid(dataUuid);
        String processStatus = processVariables.getOrDefault(FlowableConstant.PROCESS_STATUS_SING_KEY, "").toString();
        lowcodeUpdateStateDomain.setProcessStatus(processStatus);
        String formId = processVariables.get(Constants.VARIABLE_FORM_ID).toString();
        String applicationId = processVariables.getOrDefault(Constants.VARIABLE_APPLICATION_ID, "").toString();
        FormVO info = formService.info(formId, applicationId);
        lowcodeUpdateStateDomain.setCollection(info.getTableName());
        LowcodeDataDomain lowcodeDataDomain = formMongoDbService.info(dataUuid, info.getId(), info.getApplicationId());

        if ("first".equals(task.getTaskDefinitionKey())) {
            lowcodeUpdateStateDomain.setStatus(FormDataStatusEnum.DRAFT.name());
        }
        mongoDbService.updateData(lowcodeUpdateStateDomain);
        // 发送短信
        messageExecutor.execute(() -> sendMessage(task, processVariables, lowcodeDataDomain, info));
    }


    private void sendWecomMessage(FlowableSendMessageRequest flowableSendMessageRequest,
                                  LowcodeDataDomain lowcodeDataDomain, FormVO formVO) {
        JSONObject instValue = lowcodeDataDomain.getInstValue();
        instValue.put("formName", formVO.getFormName());
        FormUser formUser =
                JSONObject.parseObject(JSONObject.toJSONString(lowcodeDataDomain.getCreator()), FormUser.class);
        instValue.put("creator", formUser.getAssigneeName());
        instValue.put("taskName", flowableSendMessageRequest.getName());
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        String auditUrl;
        ApplicationVO applicationVO = applicationService.detail(lowcodeDataDomain.getApplicationId());
        String suitId = SuiteApplicationEnum.getSuiteIdByApp(applicationVO.getTemplateId());
        String domainName = ConfigCache.getValue(ConfigEnum.LOWCODE_PUBLIC_PUBLISH_URL.name());
        if (CompanyDataSourceEnum.WECOM.name().equals(info.getDataSource())) {
            auditUrl = domainName + "smallProgram/companyWxLogin?id=%s&redirectUrl=%s";
            instValue.put("auditUrl", String.format(auditUrl, info.getCompanyUuid(),
                    URLEncoder.encode(flowableSendMessageRequest.getAuditUrl())));
        } else {
            auditUrl = domainName + "smallProgram/companyWxLoginThree?redirect=%s&suiteId=%s";
            instValue.put("auditUrl",
                    String.format(auditUrl, URLEncoder.encode(flowableSendMessageRequest.getAuditUrl()), suitId));
        }
        List<Markdown> configList = MessageUtils.getMarkDown(flowableSendMessageRequest.getMarkdownList(), instValue);
        com.wuji.message.model.request.SendMessageRequest messageRequest =
                new com.wuji.message.model.request.SendMessageRequest();
        messageRequest.setUserIdList(flowableSendMessageRequest.getAssigneeList());
        messageRequest.setMessageType("markdown");
        messageRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        messageRequest.setMessage(FormMarkdownUtils.markContent(configList));
        messageRequest.setSuiteId(suitId);
        messageContext.getHandler(info.getDataSource()).sendMessage(messageRequest);
    }

    @Override
    public Map<String, Boolean> checkCondition(ProcessInstance processInstance, ModelVO modelVO, String pid) {
        Map<String, Object> processVariables = processInstance.getProcessVariables();
        String uuid = processVariables.getOrDefault(Constants.VARIABLE_DATA_UUID, "").toString();
        List<FlowableActivityConfigDomain> conditionConfigList =
                flowableActivityConfigService.getConditionConfig(modelVO.getModelId(), pid);
        String formId = processVariables.get(Constants.VARIABLE_FORM_ID).toString();
        Map<String, Boolean> idToConditionMap = new HashMap<>();
        String defaultFlow = null;
        String applicationId = processVariables.getOrDefault(Constants.VARIABLE_APPLICATION_ID, "").toString();
        LowcodeDataDomain info = formMongoDbService.info(uuid, formId, applicationId);
        JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(info);
        for (FlowableActivityConfigDomain flowableActivityConfigDomain : conditionConfigList) {
            FlowableMongodbSearchFilter conditionConfigJson = flowableActivityConfigDomain.getConditionConfigJson();
            if (conditionConfigJson == null) {
                if ("condition".equals(flowableActivityConfigDomain.getActivityType()) ||
                        "other".equals(flowableActivityConfigDomain.getActivityType())) {
                    defaultFlow = flowableActivityConfigDomain.getActivityId();
                }
                // idToConditionMap.put(flowableActivityConfigDomain.getActivityId(), Boolean.TRUE);
            } else {
                MongodbSearchFilter filter = AbstractFormWorkflowConverter.INSTANCE.toFilter(conditionConfigJson);
                Boolean result = MongoSearchUtils.checkData(filter, jsonObject);
                idToConditionMap.put(flowableActivityConfigDomain.getActivityId(), result);
            }
        }
        if (idToConditionMap.containsValue(Boolean.TRUE)) {
            idToConditionMap.put(defaultFlow, Boolean.FALSE);
        } else {
            idToConditionMap.put(defaultFlow, Boolean.TRUE);
        }
        return idToConditionMap;
    }

    @Override
    public void taskFinish(HistoricTaskInstance historicTaskInstance, Map<String, Object> processVariables) {
        String status = processVariables.getOrDefault(FlowableConstant.PROCESS_STATUS_SING_KEY, "").toString();
        String dataUuid = processVariables.get(Constants.VARIABLE_DATA_UUID).toString();
        String formId = processVariables.get(Constants.VARIABLE_FORM_ID).toString();
        String applicationId = processVariables.get(Constants.VARIABLE_APPLICATION_ID).toString();
        if ("first".equals(historicTaskInstance.getTaskDefinitionKey())) {
            LowcodeUpdateStateDomain lowcodeUpdateStateDomain = new LowcodeUpdateStateDomain();
            lowcodeUpdateStateDomain.setUuid(dataUuid);
            lowcodeUpdateStateDomain.setStatus(FormDataStatusEnum.APPROVING.name());
            FormVO info = formService.info(formId, applicationId);
            lowcodeUpdateStateDomain.setCollection(info.getTableName());
            mongoDbService.updateData(lowcodeUpdateStateDomain);
        }
        LowcodeDataDomain lowcodeDataDomain = formMongoDbService.info(dataUuid, formId, applicationId);
        JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(lowcodeDataDomain);
        if (historicTaskInstance.getAssignee().equals(UserUtils.getUser().getUserId())) {
            workFlowService.copy(historicTaskInstance, processVariables, jsonObject);
            FormDataStreamTrigger formDataStreamTrigger = new FormDataStreamTrigger();
            formDataStreamTrigger.setActivityId(historicTaskInstance.getTaskDefinitionKey());
            formDataStreamTrigger.setActivityAction(status);
            formDataStreamPublishService.trigger(formId, "activity_finish", lowcodeDataDomain, applicationId,
                    formDataStreamTrigger);
        }
    }

    // @Override
    // public void firstNodeStart(ProcessInstance processInstance) {
    //     Map<String, Object> processVariables = processInstance.getProcessVariables();
    //     String dataUuid = processVariables.get(Constants.VARIABLE_DATA_UUID).toString();
    //     String formId = processVariables.get(Constants.VARIABLE_FORM_ID).toString();
    //     String applicationId = processVariables.get(Constants.VARIABLE_APPLICATION_ID).toString();
    //     LowcodeUpdateStateDomain lowcodeUpdateStateDomain = new LowcodeUpdateStateDomain();
    //     lowcodeUpdateStateDomain.setUuid(dataUuid);
    //     lowcodeUpdateStateDomain.setStatus(FormDataStatusEnum.DRAFT.name());
    //     FormVO info = formService.info(formId, applicationId);
    //     lowcodeUpdateStateDomain.setCollection(info.getTableName());
    //     mongoDbService.updateData(lowcodeUpdateStateDomain);
    // }

    private void sendMessage(Task task, Map<String, Object> processVariables, LowcodeDataDomain lowcodeDataDomain,
                             FormVO info) {
        ModelVO modelVO = modelManageService.infoByProcessDefinitionId(task.getProcessDefinitionId());
        FlowableRemindConfig remindConfig =
                flowableActivityConfigService.getRemindConfig(task.getTaskDefinitionKey(), modelVO.getModelId());
        if (remindConfig != null) {
            if (remindConfig.getSendEmail()) {
                emailService.sendEmail(remindConfig.getAssigneeUserList(), remindConfig.getEmailContent(),
                        remindConfig.getTitle());
            }
            String auditUrl = getAuditUrl(task.getTaskDefinitionKey(), lowcodeDataDomain);
            if (remindConfig.getInMail()) {
                sendInEmail(task, processVariables, lowcodeDataDomain, modelVO, auditUrl);
            }
            FlowableSendMessageRequest flowableSendMessageRequest = new FlowableSendMessageRequest();
            flowableSendMessageRequest.setName(task.getName());
            flowableSendMessageRequest.setAssigneeList(Collections.singletonList(Long.valueOf(task.getAssignee())));
            flowableSendMessageRequest.setAuditUrl(auditUrl);
            if (remindConfig.getSendWeCom()) {
                flowableSendMessageRequest.setMarkdownList(
                        getMessageMarkdown(ConfigEnum.LOWCODE_WECOM_TASK_START.name()));
                sendWecomMessage(flowableSendMessageRequest, lowcodeDataDomain, info);
            }
            if (remindConfig.getSendDingTalk()) {
                CompanyVO companyVO = companyService.info(UserUtils.getUser().getCompanyId());
                messageExecutor.execute(() -> sendDingTalkMessage(companyVO, lowcodeDataDomain, remindConfig,
                        flowableSendMessageRequest));
            }
        }
    }

    private void sendInEmail(Task task, Map<String, Object> processVariables, LowcodeDataDomain lowcodeDataDomain,
                             ModelVO modelVO, String auditUrl) {
        List<String> assigneeUserList =
                flowableActivityConfigService.getAssigneeUserList(task.getTaskDefinitionKey(), modelVO.getModelId(),
                        processVariables);
        List<Long> userIdList = assigneeUserList.stream().map(Long::valueOf).collect(Collectors.toList());
        userIdList = userIdList.stream().distinct().collect(Collectors.toList());
        String content =
                "<p>您${applicationName}有一条待办事项，请点击<a href=\"${auditUrl}\" target=\"_blank\">查看详情</a>尽快处理，谢谢~</p>";
        Map<String, Object> paramMap = new HashMap<>();
        ApplicationVO detail = applicationService.detail(lowcodeDataDomain.getApplicationId());
        paramMap.put("applicationName", detail.getApplicationName());
        paramMap.put("url", auditUrl);
        content = MessageTemplateUtils.dealMessage(content, paramMap);
        MessageInsertRequest messageInsertRequest = new MessageInsertRequest();
        messageInsertRequest.setUserIdList(userIdList);
        messageInsertRequest.setContent(content);
        messageInsertRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        messageInsertRequest.setSource("FLOWABLE");
        messageInsertRequest.setMessageType(InMailMessageTypeEnum.FLOWABLE_NOTICE.name());
        messageService.insert(messageInsertRequest);
    }

    private static String getAuditUrl(String taskDefinitionKey, LowcodeDataDomain lowcodeDataDomain) {
        String auditConfig =
                "approvalPage/" + lowcodeDataDomain.getApplicationId() + "/" + lowcodeDataDomain.getFormId() + "/" +
                        lowcodeDataDomain.getUuid() + "/" + taskDefinitionKey;
        return ConfigCache.getValue(ConfigEnum.LOWCODE_PUBLIC_PUBLISH_URL.name()) + auditConfig;
    }

    private void updateState(Map<String, Object> variables, FormDataStatusEnum pass, String processInstanceId) {
        String dataUuid = variables.get(Constants.VARIABLE_DATA_UUID).toString();
        String formId = variables.get(Constants.VARIABLE_FORM_ID).toString();
        String applicationId = variables.get(Constants.VARIABLE_APPLICATION_ID).toString();
        FormVO info = formService.info(formId, applicationId);
        LowcodeUpdateStateDomain lowcodeUpdateStateDomain = new LowcodeUpdateStateDomain();
        lowcodeUpdateStateDomain.setStatus(pass.name());
        lowcodeUpdateStateDomain.setUuid(dataUuid);
        lowcodeUpdateStateDomain.setProcessInstanceId(processInstanceId);
        lowcodeUpdateStateDomain.setCollection(info.getTableName());
        mongoDbService.updateData(lowcodeUpdateStateDomain);
    }

    private static LowcodeUpdateStateDomain getLowcodeUpdateStateDomain(String dataUuid, FormVO info, String status) {
        LowcodeUpdateStateDomain lowcodeUpdateStateDomain = new LowcodeUpdateStateDomain();
        lowcodeUpdateStateDomain.setUuid(dataUuid);
        lowcodeUpdateStateDomain.setCollection(info.getTableName());
        if (ProcessStateEnum.TERMINATED.getStatus().equals(status)) {
            lowcodeUpdateStateDomain.setStatus(FormDataStatusEnum.NO_PASS.name());
            lowcodeUpdateStateDomain.setProcessStatus(ProcessStateEnum.TERMINATED.getStatus());
        } else {
            lowcodeUpdateStateDomain.setStatus(FormDataStatusEnum.PASS.name());
            lowcodeUpdateStateDomain.setProcessStatus(ProcessStateEnum.COMPLETED.getStatus());
        }
        return lowcodeUpdateStateDomain;
    }

    private void dataTransAndCallBackWhileComplete(LowcodeDataDomain lowcodeDataDomain,
                                                   Map<String, Object> processVariables) {
        ParentInfoMongodbDomain parentInfo = lowcodeDataDomain.getParentInfo();
        if (parentInfo != null) {
            if (parentInfo.getCallBack()) {
                String processInstanceParentId =
                        processVariables.getOrDefault(Constants.VARIABLE_PARENT_PROCESS_INSTANCE_ID, "").toString();
                String processInstanceTaskId =
                        processVariables.getOrDefault(Constants.VARIABLE_PARENT_TASK_ID, "").toString();
                String applicationId = processVariables.getOrDefault(Constants.VARIABLE_APPLICATION_ID, "").toString();
                FlowableAuditRequest flowableAuditRequest = new FlowableAuditRequest();
                flowableAuditRequest.setTaskId(processInstanceTaskId);
                flowableAuditRequest.setProcessInstanceId(processInstanceParentId);
                flowableAuditRequest.setComment("任务完成");
                workFlowService.taskComplete(flowableAuditRequest, Boolean.FALSE);
                LowcodeDataDomain parentDomain =
                        formMongoDbService.infoByProcessInstanceId(parentInfo.getParentFormId(),
                                parentInfo.getParentProcessInstanceId(), applicationId);
                FlowableSubFlowConfig flowableSubConfig =
                        flowableActivityConfigService.getFlowableSubConfig(parentInfo.getParentActivityId(),
                                parentInfo.getParentModelId());
                JSONObject targetJson = MongoDbDataTransUtils.dataTrans(lowcodeDataDomain,
                        flowableSubConfig.getChildFlowableDataTrans(), parentDomain);
                // 子传父进行保存
                FormVO info = formService.info(parentInfo.getParentFormId(), lowcodeDataDomain.getApplicationId());
                LowcodeUpdateStateDomain lowcodeUpdateStateDomain = new LowcodeUpdateStateDomain();
                lowcodeUpdateStateDomain.setUuid(parentInfo.getParentDataUuid());
                lowcodeUpdateStateDomain.setCollection(info.getTableName());
                lowcodeUpdateStateDomain.setInstValue(targetJson);
                mongoDbService.updateData(lowcodeUpdateStateDomain);
            }

        }
    }

    public List<FormMessageMarkdown> getMessageMarkdown(String key) {
        ConfigVO configVO = configService.detailByKey(key);
        return JSONArray.parseArray(configVO.getConfigValue(), FormMessageMarkdown.class);
    }
}
