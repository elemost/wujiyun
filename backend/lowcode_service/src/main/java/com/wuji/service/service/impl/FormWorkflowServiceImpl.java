package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.TaskProgressHolder;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractFormWorkflowConverter;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.model.domain.FormWorkflowDataDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.ImportProgress;
import com.wuji.service.model.request.FormFlowableCopyRequest;
import com.wuji.service.model.request.FormFlowableDoneListRequest;
import com.wuji.service.model.request.FormFlowableOwnerListRequest;
import com.wuji.service.model.request.FormFlowableQueryCommonRequest;
import com.wuji.service.model.request.FormFlowableToDoListRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormWorkflowCreateTaskRequest;
import com.wuji.service.model.request.TaskCreateRequest;
import com.wuji.service.model.request.WorkFlowBatchAuditRequest;
import com.wuji.service.model.request.WorkFlowTaskRequest;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormDoneTaskVO;
import com.wuji.service.model.vo.FormFlowableCopyVO;
import com.wuji.service.model.vo.FormFlowableStatisticVO;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.service.model.vo.FormOwnerTaskVO;
import com.wuji.service.model.vo.FormPendingCountVO;
import com.wuji.service.model.vo.FormPendingTaskVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.FormModelService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.FormWorkflowService;
import com.wuji.service.service.TaskService;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.enums.ProcessStateEnum;
import com.wuji.workflow.model.info.FlowableFormFieldConfig;
import com.wuji.workflow.model.request.FlowableAuditRequest;
import com.wuji.workflow.model.request.FlowableCopyRequest;
import com.wuji.workflow.model.request.FlowableDoneListRequest;
import com.wuji.workflow.model.request.FlowableOwnerListRequest;
import com.wuji.workflow.model.request.FlowableToDoListRequest;
import com.wuji.workflow.model.vo.DoneTaskVO;
import com.wuji.workflow.model.vo.FlowableCopyVO;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.model.vo.OwnerTaskVO;
import com.wuji.workflow.model.vo.PendingTaskVO;
import com.wuji.workflow.service.FlowableCopyService;
import com.wuji.workflow.service.ModelManageService;
import com.wuji.workflow.service.WorkFlowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormWorkflowServiceImpl implements FormWorkflowService {

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private FlowableCopyService flowableCopyService;

    @Autowired
    private FormModelService formModelService;

    @Autowired
    private ModelManageService modelManageService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ThreadPoolExecutor mongoTaskExecutor;

    @Autowired
    private FormService formService;

    @Override
    public void createTask(FormWorkflowCreateTaskRequest formWorkflowCreateTaskRequest) {
        FormModelVO formModelVO = formModelService.info(formWorkflowCreateTaskRequest.getFormId(),
                formWorkflowCreateTaskRequest.getApplicationId());
        if (formModelVO == null || StringUtils.isEmpty(formModelVO.getModelId()) ||
                "DOWN".equals(formModelVO.getStatus())) {
            return;
        } else {
            ModelVO info = modelManageService.info(formModelVO.getModelId());
            if (StringUtils.isEmpty(info.getDeploymentId())) {
                return;
            } else {
                if (StringUtils.isEmpty(formWorkflowCreateTaskRequest.getProcessInstanceId())) {
                    HashMap<String, Object> variables = new HashMap<>();
                    variables.put(FlowableConstant.FLOWABLE_CALLBACK_SERVICE, Constants.VARIABLE_FORM_SERVICE);
                    variables.put(Constants.VARIABLE_CREATE_FORM_ID, formWorkflowCreateTaskRequest.getCreateFormId());
                    variables.put(Constants.VARIABLE_FORM_ID, formWorkflowCreateTaskRequest.getFormId());
                    variables.put(Constants.VARIABLE_FORM_VERSION, formWorkflowCreateTaskRequest.getVersion());
                    variables.put(Constants.VARIABLE_DATA_UUID, formWorkflowCreateTaskRequest.getUuid());
                    variables.put(FlowableConstant.PROCESS_STATUS_SING_KEY, ProcessStateEnum.RUNNING.getStatus());
                    variables.put(Constants.VARIABLE_APPLICATION_ID, formWorkflowCreateTaskRequest.getApplicationId());
                    variables.put(Constants.VARIABLE_PARENT_TASK_ID, formWorkflowCreateTaskRequest.getParentTaskId());
                    variables.put(Constants.VARIABLE_PARENT_PROCESS_INSTANCE_ID,
                            formWorkflowCreateTaskRequest.getParentProcessInstanceId());
                    variables.putAll(formWorkflowCreateTaskRequest.getInstValue().getInnerMap());
                    workFlowService.createTask(info.getDeploymentId(), formWorkflowCreateTaskRequest.getCreateUserId(),
                            variables);
                } else {
                    workFlowService.finishFirstAudit(formWorkflowCreateTaskRequest.getProcessInstanceId());
                }
            }
        }
    }

    @Override
    public QueryPageVO<FormPendingTaskVO> getPendingList(FormFlowableToDoListRequest formFlowableToDoListRequest) {
        if (StringUtils.isNotEmpty(formFlowableToDoListRequest.getFormId()) &&
                StringUtils.isNotEmpty(formFlowableToDoListRequest.getKeyword())) {
            FieldExistNameVO fieldExistNameVO =
                    formService.getAllFormConfigCommonList(formFlowableToDoListRequest.getFormId(), Boolean.FALSE,
                            formFlowableToDoListRequest.getApplicationId(), Boolean.FALSE);
            List<String> fields = fieldExistNameVO.getFields().stream()
                    .filter(c -> FormFieldTypeEnum.searchFieldType().contains(c.getType()))
                    .map(FormConfigCommon::getName).collect(Collectors.toList());
            FormSearchDataRequest formSearchDataRequest = new FormSearchDataRequest();
            formSearchDataRequest.setFormId(formFlowableToDoListRequest.getFormId());
            formSearchDataRequest.setKeyword(formFlowableToDoListRequest.getKeyword());
            formSearchDataRequest.setKeyList(fields);
            formSearchDataRequest.setPageSize(1000);
            formSearchDataRequest.setApplicationId(formFlowableToDoListRequest.getApplicationId());
            formSearchDataRequest.setStatus(FormDataStatusEnum.APPROVING.name());
            QueryPageVO<LowcodeDataDomain> lowcodeDataDomainQueryPageVO =
                    formMongoDbService.queryListDomain(formSearchDataRequest);
            List<String> processInstanceIds =
                    lowcodeDataDomainQueryPageVO.getList().stream().map(LowcodeDataDomain::getProcessInstanceId)
                            .filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            formFlowableToDoListRequest.setProcessInstanceIds(processInstanceIds);
            if (CollectionUtils.isEmpty(processInstanceIds)) {
                return new QueryPageVO<>();
            }
        }
        FlowableToDoListRequest flowableToDoListRequest =
                AbstractFormWorkflowConverter.INSTANCE.toRequest(formFlowableToDoListRequest);
        HashMap<String, Object> variableMap = buildVariable(formFlowableToDoListRequest);
        flowableToDoListRequest.setParams(variableMap);
        QueryPageVO<PendingTaskVO> todoList = workFlowService.getTodoList(flowableToDoListRequest);
        if (todoList.getTotal() == 0) {
            return new QueryPageVO<>(formFlowableToDoListRequest.getPageNum(),
                    formFlowableToDoListRequest.getPageSize(), 0, new ArrayList<>());
        }
        Map<String, String> formNameMap = getFormNameMap(
                todoList.getList().stream().map(PendingTaskVO::getProcessVariables).collect(Collectors.toList()),
                formFlowableToDoListRequest.getApplicationId());
        Map<String, String> applicationNameMap = getApplicationNameMap(
                todoList.getList().stream().map(PendingTaskVO::getProcessVariables).collect(Collectors.toList()));
        List<FormPendingTaskVO> formPendingTaskVOList = new ArrayList<>();
        List<String> applicationIdList = new ArrayList<>();
        List<String> formIdList = new ArrayList<>();
        for (PendingTaskVO pendingTaskVO : todoList.getList()) {
            Map<String, Object> processVariables = pendingTaskVO.getProcessVariables();
            Object applicationId = processVariables.get(Constants.VARIABLE_APPLICATION_ID);
            Object formId = processVariables.get(Constants.VARIABLE_FORM_ID);
            if (applicationId != null && formId != null) {
                applicationIdList.add(applicationId.toString());
                formIdList.add(formId.toString());
            }
        }
        List<FieldExistNameVO> allFormFieldVO =
                formService.getAllFormFieldVO(applicationIdList, formIdList, Boolean.FALSE, true);
        Map<String, FieldExistNameVO> formKeyToFieldMap = allFormFieldVO.stream()
                .collect(Collectors.toMap(c -> c.getFormId() + "_" + c.getApplicationId(), c -> c));
        Map<String, FormWorkflowDataDomain> tableNameMap = new HashMap<>();
        for (PendingTaskVO pendingTaskVO : todoList.getList()) {
            Map<String, Object> processVariables = pendingTaskVO.getProcessVariables();
            Object applicationId = processVariables.get(Constants.VARIABLE_APPLICATION_ID);
            Object formId = processVariables.get(Constants.VARIABLE_FORM_ID);
            FieldExistNameVO formFieldVO = formKeyToFieldMap.get(formId + "_" + applicationId);
            FormWorkflowDataDomain formWorkflowDataDomain =
                    tableNameMap.getOrDefault(applicationId + "_" + formId, new FormWorkflowDataDomain());
            formWorkflowDataDomain.setFormId(formId.toString());
            formWorkflowDataDomain.setApplicationId(applicationId.toString());
            formWorkflowDataDomain.setTableName(formFieldVO.getTableName());
            formWorkflowDataDomain.getUuids()
                    .add(processVariables.getOrDefault(Constants.VARIABLE_DATA_UUID, "").toString());
            List<FlowableFormFieldConfig> fieldConfigList = pendingTaskVO.getFieldConfigList();
            if (CollectionUtils.isNotEmpty(fieldConfigList)) {
                List<FlowableFormFieldConfig> flowableFormFieldConfigs =
                        fieldConfigList.stream().filter(FlowableFormFieldConfig::getBriefingFlag)
                                .collect(Collectors.toList());
                formWorkflowDataDomain.setFlowableFormFieldConfigs(flowableFormFieldConfigs);
            }
            if (CollectionUtils.isNotEmpty(formWorkflowDataDomain.getFlowableFormFieldConfigs())) {
                tableNameMap.put(applicationId + "_" + formId, formWorkflowDataDomain);
            }
        }
        List<FormWorkflowDataDomain> formWorkflowDataDomains = new ArrayList<>(tableNameMap.values());
        List<LowcodeDataVO> workflowDataList = formMongoDbService.getWorkflowData(formWorkflowDataDomains);
        Map<String, LowcodeDataVO> uuidToVOMap = workflowDataList.stream()
                .collect(Collectors.toMap(c -> c.getUuid() + "_" + c.getApplicationId() + "_" + c.getFormId(), c -> c));
        for (PendingTaskVO pendingTaskVO : todoList.getList()) {
            FormPendingTaskVO formPendingTaskVO = AbstractFormWorkflowConverter.INSTANCE.toVO(pendingTaskVO);
            Map<String, Object> processVariables = formPendingTaskVO.getProcessVariables();
            Object formId = processVariables.getOrDefault(Constants.VARIABLE_FORM_ID, "");
            Object applicationId = processVariables.getOrDefault(Constants.VARIABLE_APPLICATION_ID, "");
            Object uuid = processVariables.getOrDefault(Constants.VARIABLE_DATA_UUID, "");
            String formName = getName(processVariables, formNameMap, Constants.VARIABLE_FORM_ID);
            String applicationName = getName(processVariables, applicationNameMap, Constants.VARIABLE_APPLICATION_ID);
            formPendingTaskVO.setApplicationName(applicationName);
            formPendingTaskVO.setFormName(formName);
            formPendingTaskVO.setApplicationId(applicationId.toString());
            formPendingTaskVO.setOtherConfig(pendingTaskVO.getOtherConfig());
            formPendingTaskVO.setFieldConfigList(null);
            String key = uuid + "_" + applicationId + "_" + formId;
            LowcodeDataVO lowcodeDataVO = uuidToVOMap.get(key);
            if (lowcodeDataVO != null) {
                FieldExistNameVO formFieldVO =
                        formKeyToFieldMap.get(lowcodeDataVO.getFormId() + "_" + lowcodeDataVO.getApplicationId());
                FormWorkflowDataDomain formWorkflowDataDomain = tableNameMap.get(applicationId + "_" + formId);
                if (formWorkflowDataDomain != null) {
                    List<FlowableFormFieldConfig> flowableFormFieldConfigs = new ArrayList<>();
                    JSONObject instValue = lowcodeDataVO.getInstValue();
                    Map<String, String> nameToLabelMap = formFieldVO.getFields().stream()
                            .collect(Collectors.toMap(FormConfigCommon::getName, FormConfigCommon::getLabel));

                    for (FlowableFormFieldConfig flowableFormFieldConfig : formWorkflowDataDomain.getFlowableFormFieldConfigs()) {
                        FlowableFormFieldConfig finalConfig = JSONObject.parseObject(JSONObject.toJSONString(flowableFormFieldConfig), FlowableFormFieldConfig.class);
                        finalConfig.setLabel(nameToLabelMap.get(finalConfig.getName()));
                        finalConfig.setValue(instValue.get(finalConfig.getName()));
                        flowableFormFieldConfigs.add(finalConfig);
                    }
                    formPendingTaskVO.setFieldConfigList(flowableFormFieldConfigs);
                }
            }
            formPendingTaskVOList.add(formPendingTaskVO);
        }
        return new QueryPageVO<>(formFlowableToDoListRequest.getPageNum(), formFlowableToDoListRequest.getPageSize(),
                todoList.getTotal(), formPendingTaskVOList);
    }

    @Override
    public List<FormPendingCountVO> getFormPendingCount(FormFlowableToDoListRequest formFlowableToDoListRequest) {
        FlowableToDoListRequest flowableToDoListRequest =
                AbstractFormWorkflowConverter.INSTANCE.toRequest(formFlowableToDoListRequest);
        HashMap<String, Object> variableMap = buildVariable(formFlowableToDoListRequest);
        flowableToDoListRequest.setParams(variableMap);
        flowableToDoListRequest.setPageSize(10);
        QueryPageVO<PendingTaskVO> todoList = workFlowService.getTodoListOnlyTask(flowableToDoListRequest);
        if (todoList.getTotal() == 0) {
            return new ArrayList<>();
        }
        List<String> processDefKeys =
                todoList.getList().stream().map(PendingTaskVO::getProcDefKey).collect(Collectors.toList());
        List<FormModelVO> formModelVOS = formModelService.getByBusinessTypes(processDefKeys);
        List<String> applicationIds =
                formModelVOS.stream().map(FormModelVO::getApplicationId).collect(Collectors.toList());
        List<FormPendingTaskVO> formPendingTaskVOList = new ArrayList<>();
        for (PendingTaskVO pendingTaskVO : todoList.getList()) {
            FormPendingTaskVO formPendingTaskVO = AbstractFormWorkflowConverter.INSTANCE.toVO(pendingTaskVO);
            Map<String, Object> processVariables = formPendingTaskVO.getProcessVariables();
            Object formId = processVariables.getOrDefault(Constants.VARIABLE_FORM_ID, "");
            Object applicationId = processVariables.getOrDefault(Constants.VARIABLE_APPLICATION_ID, "");
            formPendingTaskVO.setFormId(formId.toString());
            formPendingTaskVO.setApplicationId(applicationId.toString());
            formPendingTaskVOList.add(formPendingTaskVO);
        }
        Map<String, List<FormPendingTaskVO>> applicationFormIdMap = formPendingTaskVOList.stream()
                .collect(Collectors.groupingBy(c -> c.getApplicationId() + "_" + c.getFormId()));
        List<ApplicationVO> applicationVOS = applicationService.getApplicationByIdList(applicationIds);
        List<String> formIds =
                formPendingTaskVOList.stream().map(FormPendingTaskVO::getFormId).collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList =
                applicationCategoryService.getByIdList(formIds, applicationIds);
        Map<String, ApplicationCategoryVO> applicationFormIdToCategoryMap = applicationCategoryVOList.stream()
                .collect(Collectors.toMap(c -> c.getApplicationId() + "_" + c.getId(), c -> c));
        Map<String, String> applicationMap = applicationVOS.stream()
                .collect(Collectors.toMap(ApplicationVO::getId, ApplicationVO::getApplicationName));
        List<FormPendingCountVO> formPendingCountVOList = new ArrayList<>();
        applicationFormIdMap.forEach((key, taskVOS) -> {
            String[] split = key.split("_");
            FormPendingCountVO formPendingCountVO = new FormPendingCountVO();
            formPendingCountVO.setApplicationId(split[0]);
            formPendingCountVO.setFormId(split[1]);
            formPendingCountVO.setPendingCount(taskVOS.size());
            formPendingCountVO.setApplicationName(applicationMap.get(split[0]));
            ApplicationCategoryVO applicationCategoryVO = applicationFormIdToCategoryMap.get(
                    formPendingCountVO.getApplicationId() + "_" + formPendingCountVO.getFormId());
            if (applicationCategoryVO != null) {
                formPendingCountVO.setFormName(applicationCategoryVO.getCategoryName());
                formPendingCountVOList.add(formPendingCountVO);
            }
        });


        return formPendingCountVOList;
    }

    @Override
    public Integer pendingCount(FormFlowableToDoListRequest formFlowableToDoListRequest) {
        FlowableToDoListRequest flowableToDoListRequest =
                AbstractFormWorkflowConverter.INSTANCE.toRequest(formFlowableToDoListRequest);
        HashMap<String, Object> variableMap = buildVariable(formFlowableToDoListRequest);
        flowableToDoListRequest.setParams(variableMap);
        QueryPageVO<PendingTaskVO> todoList = workFlowService.getTodoList(flowableToDoListRequest);
        return todoList.getTotal();
    }

    private static HashMap<String, Object> buildVariable(
            FormFlowableQueryCommonRequest formFlowableQueryCommonRequest) {
        HashMap<String, Object> variables = new HashMap<>();
        if (formFlowableQueryCommonRequest.getStartTime() != null) {
            variables.put("beginTime", TimeUtils.formatDateTime(formFlowableQueryCommonRequest.getStartTime()));
        }
        if (formFlowableQueryCommonRequest.getEndTime() != null) {
            variables.put("endTime", TimeUtils.formatDateTime(formFlowableQueryCommonRequest.getEndTime()));
        }
        if (StringUtils.isNotEmpty(formFlowableQueryCommonRequest.getStatus())) {
            variables.put(FlowableConstant.PROCESS_STATUS_SING_KEY, formFlowableQueryCommonRequest.getStatus());
        }
        if (StringUtils.isNotEmpty(formFlowableQueryCommonRequest.getFormId())) {
            variables.put(FlowableConstant.FORM_ID, formFlowableQueryCommonRequest.getFormId());
        }
        return variables;
    }

    @Override
    public QueryPageVO<FormOwnerTaskVO> getOwnerList(FormFlowableOwnerListRequest formFlowableOwnerListRequest) {
        FlowableOwnerListRequest flowableOwnerListRequest =
                AbstractFormWorkflowConverter.INSTANCE.toRequest(formFlowableOwnerListRequest);
        HashMap<String, Object> variableMap = buildVariable(formFlowableOwnerListRequest);
        flowableOwnerListRequest.setParams(variableMap);
        QueryPageVO<OwnerTaskVO> ownerList = workFlowService.getOwnerList(flowableOwnerListRequest);
        if (ownerList.getTotal() == 0) {
            return new QueryPageVO<>(formFlowableOwnerListRequest.getPageNum(),
                    formFlowableOwnerListRequest.getPageSize(), 0, new ArrayList<>());
        }
        Map<String, String> formNameMap = getFormNameMap(
                ownerList.getList().stream().map(OwnerTaskVO::getProcessVariables).collect(Collectors.toList()),
                formFlowableOwnerListRequest.getApplicationId());
        List<FormOwnerTaskVO> formOwnerTaskVOList = new ArrayList<>();
        Map<String, String> applicationNameMap = getApplicationNameMap(
                ownerList.getList().stream().map(OwnerTaskVO::getProcessVariables).collect(Collectors.toList()));
        for (OwnerTaskVO ownerTaskVO : ownerList.getList()) {
            FormOwnerTaskVO formOwnerTaskVO = AbstractFormWorkflowConverter.INSTANCE.toVO(ownerTaskVO);
            String formName = getName(formOwnerTaskVO.getProcessVariables(), formNameMap, Constants.VARIABLE_FORM_ID);
            String applicationName = getName(formOwnerTaskVO.getProcessVariables(), applicationNameMap,
                    Constants.VARIABLE_APPLICATION_ID);
            formOwnerTaskVO.setApplicationName(applicationName);
            formOwnerTaskVO.setFormName(formName);
            formOwnerTaskVO.setApplicationId(
                    formOwnerTaskVO.getProcessVariables().getOrDefault(Constants.VARIABLE_APPLICATION_ID, "")
                            .toString());
            formOwnerTaskVOList.add(formOwnerTaskVO);
        }
        return new QueryPageVO<>(formFlowableOwnerListRequest.getPageNum(), formFlowableOwnerListRequest.getPageSize(),
                ownerList.getTotal(), formOwnerTaskVOList);
    }


    @Override
    public QueryPageVO<FormDoneTaskVO> getDoneList(FormFlowableDoneListRequest formFlowableOwnerListRequest) {
        FlowableDoneListRequest flowableDoneListRequest =
                AbstractFormWorkflowConverter.INSTANCE.toRequest(formFlowableOwnerListRequest);
        HashMap<String, Object> variableMap = buildVariable(formFlowableOwnerListRequest);
        flowableDoneListRequest.setParams(variableMap);
        QueryPageVO<DoneTaskVO> doneList = workFlowService.getDoneList(flowableDoneListRequest);
        if (doneList.getTotal() == 0) {
            return new QueryPageVO<>(formFlowableOwnerListRequest.getPageNum(),
                    formFlowableOwnerListRequest.getPageSize(), 0, new ArrayList<>());
        }
        Map<String, String> formNameMap = getFormNameMap(
                doneList.getList().stream().map(DoneTaskVO::getProcessVariables).collect(Collectors.toList()),
                formFlowableOwnerListRequest.getApplicationId());
        Map<String, String> applicationNameMap = getApplicationNameMap(
                doneList.getList().stream().map(DoneTaskVO::getProcessVariables).collect(Collectors.toList()));
        List<FormDoneTaskVO> formDoneTaskVOList = new ArrayList<>();
        for (DoneTaskVO doneTaskVO : doneList.getList()) {
            FormDoneTaskVO formDoneTaskVO = AbstractFormWorkflowConverter.INSTANCE.toVO(doneTaskVO);
            String formName = getName(formDoneTaskVO.getProcessVariables(), formNameMap, Constants.VARIABLE_FORM_ID);
            formDoneTaskVO.setFormName(formName);
            String applicationName = getName(formDoneTaskVO.getProcessVariables(), applicationNameMap,
                    Constants.VARIABLE_APPLICATION_ID);
            formDoneTaskVO.setFormName(formName);
            formDoneTaskVO.setApplicationName(applicationName);
            formDoneTaskVO.setApplicationId(
                    formDoneTaskVO.getProcessVariables().getOrDefault(Constants.VARIABLE_APPLICATION_ID, "")
                            .toString());
            formDoneTaskVOList.add(formDoneTaskVO);
        }
        return new QueryPageVO<>(formFlowableOwnerListRequest.getPageNum(), formFlowableOwnerListRequest.getPageSize(),
                doneList.getTotal(), formDoneTaskVOList);
    }

    @Override
    public QueryPageVO<FormFlowableCopyVO> getCopyList(FormFlowableCopyRequest formFlowableCopyRequest) {
        FlowableCopyRequest flowableCopyRequest =
                AbstractFormWorkflowConverter.INSTANCE.toRequest(formFlowableCopyRequest);
        QueryPageVO<FlowableCopyVO> flowableCopyVOQueryPageVO = flowableCopyService.queryList(flowableCopyRequest);
        List<String> formIdList = flowableCopyVOQueryPageVO.getList().stream().map(FlowableCopyVO::getFormId)
                .collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList =
                applicationCategoryService.getByIdList(formIdList, formFlowableCopyRequest.getApplicationId());
        Map<String, String> formNameMap = applicationCategoryVOList.stream().collect(
                Collectors.toMap(c -> c.getId() + "_" + c.getApplicationId(), ApplicationCategoryVO::getCategoryName));
        List<String> applicationIdList = applicationCategoryVOList.stream().map(ApplicationCategoryVO::getApplicationId)
                .collect(Collectors.toList());
        List<ApplicationVO> applicationVOList = applicationService.getApplicationByIdList(applicationIdList);
        Map<String, String> applicationIdToMap = applicationVOList.stream()
                .collect(Collectors.toMap(ApplicationVO::getId, ApplicationVO::getApplicationName));
        List<FormFlowableCopyVO> formFlowableCopyVOList = new ArrayList<>();
        for (FlowableCopyVO flowableCopyVO : flowableCopyVOQueryPageVO.getList()) {
            FormFlowableCopyVO formFlowableCopyVO = AbstractFormWorkflowConverter.INSTANCE.toVO(flowableCopyVO);
            formFlowableCopyVO.setFormName(
                    formNameMap.get(formFlowableCopyVO.getFormId() + "_" + formFlowableCopyVO.getApplicationId()));
            formFlowableCopyVO.setApplicationName(applicationIdToMap.get(flowableCopyVO.getApplicationId()));
            formFlowableCopyVOList.add(formFlowableCopyVO);
        }
        return new QueryPageVO<>(formFlowableCopyRequest.getPageNum(), formFlowableCopyRequest.getPageSize(),
                flowableCopyVOQueryPageVO.getTotal(), formFlowableCopyVOList);
    }

    @Override
    public FormFlowableStatisticVO flowableStatistic() {
        FlowableToDoListRequest flowableToDoListRequest = new FlowableToDoListRequest();
        flowableToDoListRequest.setPageNum(1);
        QueryPageVO<PendingTaskVO> todoList = workFlowService.getTodoList(flowableToDoListRequest);
        FormFlowableStatisticVO formFlowableStatisticVO = new FormFlowableStatisticVO();
        formFlowableStatisticVO.setPendingCount(todoList.getTotal());
        return formFlowableStatisticVO;
    }

    @Override
    public String batchComplete(WorkFlowBatchAuditRequest workFlowBatchAuditRequest) {
        TaskCreateRequest batchAudit =
                new TaskCreateRequest(null, null, "batchComplete", JSONObject.toJSONString(workFlowBatchAuditRequest));
        String task = taskService.createTask(batchAudit);
        mongoTaskExecutor.execute(() -> batchAudit(workFlowBatchAuditRequest, task, UserUtils.getUser()));
        return task;
    }

    @Override
    public String batchReject(WorkFlowBatchAuditRequest workFlowBatchAuditRequest) {
        TaskCreateRequest batchAudit =
                new TaskCreateRequest(null, null, "batchReject", JSONObject.toJSONString(workFlowBatchAuditRequest));
        String task = taskService.createTask(batchAudit);
        mongoTaskExecutor.execute(() -> batchReject(workFlowBatchAuditRequest, task, UserUtils.getUser()));
        return task;
    }

    private void batchAudit(WorkFlowBatchAuditRequest workFlowBatchAuditRequest, String taskId, UserDomain user) {
        int total = workFlowBatchAuditRequest.getTasks().size();
        int success = 0;
        int fail = 0;
        int row = 0;
        TaskProgressHolder.initProgress(taskId, total);
        for (WorkFlowTaskRequest workFlowTaskRequest : workFlowBatchAuditRequest.getTasks()) {
            try {
                UserUtils.setUser(user);
                FlowableAuditRequest flowableAuditRequest = new FlowableAuditRequest();
                flowableAuditRequest.setProcessInstanceId(workFlowTaskRequest.getProcessInstanceId());
                flowableAuditRequest.setTaskId(workFlowTaskRequest.getTaskId());
                workFlowService.taskComplete(flowableAuditRequest, Boolean.FALSE);
                TaskProgressHolder.incrementSuccessCount(taskId);
                success++;
            } catch (Exception e) {
                fail++;
                TaskProgressHolder.incrementError(taskId, row, e.getMessage());
                log.error("批量审批失败", e);
            } finally {
                UserUtils.clearUser();
            }
            row++;
        }
        JSONObject result = new JSONObject();
        result.put("fail", fail);
        result.put("success", success);
        result.put("total", total);
        taskService.taskFinish(taskId, JSONObject.toJSONString(result));
    }

    private void batchReject(WorkFlowBatchAuditRequest workFlowBatchAuditRequest, String taskId, UserDomain user) {
        UserUtils.setUser(user);
        int total = workFlowBatchAuditRequest.getTasks().size();
        int success = 0;
        int fail = 0;
        int row = 0;
        TaskProgressHolder.initProgress(taskId, total);
        for (WorkFlowTaskRequest workFlowTaskRequest : workFlowBatchAuditRequest.getTasks()) {
            try {
                FlowableAuditRequest flowableAuditRequest = new FlowableAuditRequest();
                flowableAuditRequest.setProcessInstanceId(workFlowTaskRequest.getProcessInstanceId());
                flowableAuditRequest.setTaskId(workFlowTaskRequest.getTaskId());
                flowableAuditRequest.setTargetKey("start");
                workFlowService.taskReject(flowableAuditRequest);
                TaskProgressHolder.incrementSuccessCount(taskId);
                success++;
            } catch (Exception e) {
                fail++;
                log.error("批量审批失败", e);
                TaskProgressHolder.incrementError(taskId, row, e.getMessage());
            }
            ImportProgress importProgress = new ImportProgress(success, fail);
            TaskProgressHolder.updateProgress(taskId, importProgress);
            row++;
        }
        JSONObject result = new JSONObject();
        result.put("fail", fail);
        result.put("success", success);
        result.put("total", total);
        taskService.taskFinish(taskId, JSONObject.toJSONString(result));
        UserUtils.clearUser();
    }


    private static String getName(Map<String, Object> processVariables, Map<String, String> formNameMap, String key) {
        Object formId = processVariables.get(key);
        if (formId != null) {
            if (Constants.VARIABLE_FORM_ID.equals(key)) {
                Object applicationId = processVariables.get(Constants.VARIABLE_APPLICATION_ID);
                if (applicationId != null) {
                    return formNameMap.get(formId + "_" + applicationId);
                }
            }
            return formNameMap.get(formId.toString());
        }
        return "";
    }

    private Map<String, String> getFormNameMap(List<Map<String, Object>> processVariables, String applicationId) {
        List<String> formIdList = processVariables.stream()
                .map(c -> c.get(Constants.VARIABLE_FORM_ID) == null ? "" : c.get(Constants.VARIABLE_FORM_ID).toString())
                .collect(Collectors.toList());

        List<ApplicationCategoryVO> applicationCategoryVOList =
                applicationCategoryService.getByIdList(formIdList, applicationId);
        return applicationCategoryVOList.stream().collect(
                Collectors.toMap(c -> c.getId() + "_" + c.getApplicationId(), ApplicationCategoryVO::getCategoryName));
    }

    private Map<String, String> getApplicationNameMap(List<Map<String, Object>> processVariables) {
        List<String> applicationIdList = processVariables.stream()
                .map(c -> c.get(Constants.VARIABLE_APPLICATION_ID) == null ? "" :
                        c.get(Constants.VARIABLE_APPLICATION_ID).toString()).collect(Collectors.toList());

        List<ApplicationVO> applicationVOList = applicationService.getApplicationByIdList(applicationIdList);
        return applicationVOList.stream()
                .collect(Collectors.toMap(ApplicationVO::getId, ApplicationVO::getApplicationName));
    }
}
