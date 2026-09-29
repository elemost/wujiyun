package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.AggregateIterable;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserDeptService;
import com.wuji.admin.service.UserService;
import com.wuji.common.api.FormDataFactoryExecuteApi;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.DataFactoryReturnFieldCommonVO;
import com.wuji.common.model.vo.FormDataFactoryParamVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserDeptVO;
import com.wuji.common.trans.MultiTransactional;
import com.wuji.common.utils.JsonObjectUtils;
import com.wuji.common.utils.Md5Utils;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.converter.AbstractFormConfigCommonConverter;
import com.wuji.service.converter.AbstractFormMongoDbConverter;
import com.wuji.service.converter.AbstractMongoDbConverter;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.enums.FormExtraFunctionLocationEnum;
import com.wuji.service.enums.MongodbCalculateEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.domain.DataStreamTriggerLogDomain;
import com.wuji.service.model.domain.FormPrivilegeDataScopeDomain;
import com.wuji.service.model.domain.FormWorkflowDataDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.domain.LowcodeUpdateDataDomain;
import com.wuji.service.model.domain.LowcodeUpdateStateDomain;
import com.wuji.service.model.domain.ParentInfoMongodbDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.FormExtraFunctionButton;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.mongo.LookupAggregation;
import com.wuji.service.model.mongo.MapOperation;
import com.wuji.service.model.mongo.UnionWithAggregation;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.request.FormMongoDbBatchRequest;
import com.wuji.service.model.request.FormMongoDbDeleteRequest;
import com.wuji.service.model.request.FormMongoDbLinkRequest;
import com.wuji.service.model.request.FormMongoDbSummaryFieldRequest;
import com.wuji.service.model.request.FormMongoDbSummaryRequest;
import com.wuji.service.model.request.FormMongodbLinkSelectRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormUpdateDataRequest;
import com.wuji.service.model.request.FormWorkflowCreateTaskRequest;
import com.wuji.service.model.request.MongoDbUserFilledRequest;
import com.wuji.service.model.request.MongoGroupLookUpRequest;
import com.wuji.service.model.request.TaskCreateRequest;
import com.wuji.service.model.request.factory.DataFactoryRelationRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormAggregateMongoVO;
import com.wuji.service.model.vo.FormDataLogContentSubVO;
import com.wuji.service.model.vo.FormDataLogContentVO;
import com.wuji.service.model.vo.FormDataLogVO;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.service.model.vo.FormMongoDbLinkVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.SearchFilterVO;
import com.wuji.service.model.vo.UserContextVO;
import com.wuji.service.model.vo.form.FormSubmitCheck;
import com.wuji.service.model.vo.form.MongoSameNameVO;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormDataLogService;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormDataStreamPublishService;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormModelService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.FormWorkflowService;
import com.wuji.service.service.MongoDbService;
import com.wuji.service.service.TaskService;
import com.wuji.service.utils.FormConfigUtils;
import com.wuji.service.utils.FormPrivilegeUtils;
import com.wuji.service.utils.MongoDataUtils;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import com.wuji.workflow.model.info.FlowableFormFieldConfig;
import com.wuji.workflow.model.request.FlowableAuditRequest;
import com.wuji.workflow.service.WorkFlowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormMongoDbServiceImpl extends FormMongoDbCommonServiceImpl implements FormMongoDbService {

    @Autowired
    private FormService formService;

    @Autowired
    private MongoDbService mongoDbService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormDataLogService formDataLogService;

    @Autowired
    private FormWorkflowService formWorkflowService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDeptService userDeptService;

    @Autowired
    private FormModelService formModelService;

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FormDataStreamPublishService formDataStreamPublishService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ThreadPoolExecutor mongoTaskExecutor;

    @Autowired
    private FormDataFactoryExecuteApi formDataFactoryExecuteApi;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private AdminCommonService adminCommonService;

    @Override
    @MultiTransactional(value = {"mybatisTransactionManager", "mongoTransactionManager"})
    public String insertData(FormInsertDataRequest formInsertDataRequest) {
        return mongoInsert(formInsertDataRequest);
    }

    private String mongoInsert(FormInsertDataRequest formInsertDataRequest) {
        if (StringUtils.isEmpty(formInsertDataRequest.getUuid())) {
            formInsertDataRequest.setUuid(ObjectId.getGuid());
        }
        FormVO info = formService.info(formInsertDataRequest.getFormId(), formInsertDataRequest.getApplicationId());
        return insertMongoDbData(formInsertDataRequest, info);
    }

    @Override
    public void insertDataTrigger(FormInsertDataRequest formInsertDataRequest) {
        mongoInsert(formInsertDataRequest);
    }

    @Override
    public String insertMongoDbData(FormInsertDataRequest formInsertDataRequest, FormVO info) {
        info = resolveSourceForm(info);
        UserContextVO userContext = resolveUserContext(formInsertDataRequest.getParentInfo());
        List<FormConfigCommon> formConfigCommonList = parseFormConfigList(info);
        buildFormData(formInsertDataRequest.getInstValue(), info, formConfigCommonList, "create", null);
        LowcodeDataDomain lowcodeDataDomain = buildLowcodeDataDomain(formInsertDataRequest, info, userContext);
        FormModelVO formModelVO =
                formModelService.info(formInsertDataRequest.getFormId(), formInsertDataRequest.getApplicationId());
        boolean isWorkflowActive = isWorkflowActive(formModelVO);
        if (isWorkflowActive) {
            lowcodeDataDomain.setModelId(formModelVO.getModelId());
        }
        mongoDbService.insertData(lowcodeDataDomain, info.getTableName());
        if (isWorkflowActive) {
            startWorkflow(formInsertDataRequest, info, userContext.getUserId(), formInsertDataRequest.getParentInfo(),
                    lowcodeDataDomain.getUuid());
        }
        formDataLogService.recordLog(userContext.getUser(), null, lowcodeDataDomain, info,
                formInsertDataRequest.getStatus());
        triggerDataStream(formInsertDataRequest, lowcodeDataDomain, isWorkflowActive, formModelVO);
        return lowcodeDataDomain.getUuid();
    }

    private FormVO resolveSourceForm(FormVO info) {
        if (StringUtils.isNotEmpty(info.getSourceId())) {
            return formService.info(info.getSourceId(), info.getApplicationId());
        }
        return info;
    }

    private UserContextVO resolveUserContext(ParentInfoMongodbDomain parentInfo) {
        UserDomain user = UserUtils.getUser();
        String userId = user.getUserId();
        if (parentInfo != null && StringUtils.isNotEmpty(parentInfo.getAssigneeId())) {
            UserContextVO assignedUserContext = buildAssignedUserContext(parentInfo.getAssigneeId());
            user = assignedUserContext.getUser();
            userId = assignedUserContext.getUserId();
            UserUtils.setUser(user);
        }
        return new UserContextVO(user, userId);
    }

    private List<FormConfigCommon> parseFormConfigList(FormVO info) {
        return JSONArray.parseArray(JSON.parseObject(info.getConfig()).getString("body"), FormConfigCommon.class);
    }

    private LowcodeDataDomain buildLowcodeDataDomain(FormInsertDataRequest request, FormVO info,
                                                     UserContextVO userContext) {
        LowcodeDataDomain domain = new LowcodeDataDomain();
        if (StringUtils.isNotEmpty(info.getSourceId())) {
            domain.setFormId(info.getSourceId());
            domain.setSourceFormId(info.getId());
        } else {
            domain.setFormId(info.getId());
            domain.setSourceFormId(info.getId());
        }
        domain.setApplicationId(info.getApplicationId());
        domain.setTitle(info.getFormName());
        domain.setVersion(info.getVersion());
        domain.setCompanyId(info.getCompanyId());
        long currentTime = new Date().getTime();
        domain.setCreateTime(currentTime);
        domain.setModifyTime(currentTime);
        domain.setCreator(FormUser.getCurrent(userContext.getUser()));
        domain.setModifier(FormUser.getCurrent(userContext.getUser()));
        domain.setInstValue(request.getInstValue());
        domain.setUuid(request.getUuid());
        domain.setDeptList(FormDept.getCurrentDept(userContext.getUser()));
        domain.setStatus(request.getStatus());
        domain.setParentInfo(request.getParentInfo());
        return domain;
    }

    private boolean isWorkflowActive(FormModelVO formModelVO) {
        return formModelVO != null && "UP".equals(formModelVO.getStatus());
    }

    private void startWorkflow(FormInsertDataRequest request, FormVO info, String userId,
                               ParentInfoMongodbDomain parentInfo, String uuid) {
        FormWorkflowCreateTaskRequest workflowRequest = AbstractMongoDbConverter.INSTANCE.toRequest(request);
        workflowRequest.setApplicationId(info.getApplicationId());
        workflowRequest.setCreateUserId(userId);
        if (parentInfo != null) {
            workflowRequest.setParentProcessInstanceId(parentInfo.getParentProcessInstanceId());
            workflowRequest.setParentTaskId(parentInfo.getParentTaskId());
        }
        startAudit(workflowRequest, uuid, request.getStatus(), info);
    }

    private void triggerDataStream(FormInsertDataRequest request, LowcodeDataDomain domain, boolean isWorkflowActive,
                                   FormModelVO formModelVO) {
        if (request.getTriggerParentList().size() >= Constants.MAX_TRIGGER_CYCLE) {
            return;
        }
        if (isWorkflowActive) {
            domain.setModelId(formModelVO.getModelId());
        }
        LowcodeDataDomain latestData = info(domain.getUuid(), domain.getFormId(), domain.getApplicationId());
        FormDataStreamTrigger trigger = new FormDataStreamTrigger();
        trigger.setParentList(request.getTriggerParentList());
        formDataStreamPublishService.trigger(request.getFormId(), "create", latestData, request.getApplicationId(),
                trigger);
    }

    private UserContextVO buildAssignedUserContext(String assigneeId) {
        UserDomain user = new UserDomain();
        user.setUserId(assigneeId);
        user.setCompanyId(UserUtils.getUser().getCompanyId());
        Map<Long, String> idToNameMap = userService.getIdToNameMap(Collections.singletonList(Long.valueOf(assigneeId)));
        user.setNickName(idToNameMap.get(Long.valueOf(assigneeId)));
        List<UserDeptVO> userDeptVOList =
                userDeptService.getByUserIdList(Collections.singletonList(Long.valueOf(assigneeId)), null);
        user.setDeptIdList(userDeptVOList.stream().map(UserDeptVO::getDeptId).collect(Collectors.toList()));
        return new UserContextVO(user, assigneeId);
    }

    private void buildFormData(JSONObject instValue, FormVO info, List<FormConfigCommon> formConfigCommonList,
                               String type, String uuid) {
        Set<String> allType = formDataContext.getAllType();
        FormSubmitCheck formSubmitCheck = new FormSubmitCheck();
        formSubmitCheck.setUuid(uuid);
        for (FormConfigCommon formConfigCommon : formConfigCommonList) {
            if (!allType.contains(formConfigCommon.getType())) {
                continue;
            }
            FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
            if ("create".equals(type)) {
                formDataService.dealWhileCreate(instValue, formConfigCommon, info, formSubmitCheck);
            } else {
                formDataService.dealWhileUpdate(instValue, formConfigCommon, info, formSubmitCheck);
            }
        }
        if (CollectionUtils.isNotEmpty(formSubmitCheck.getSameNameList())) {
            List<Object> currentValueList =
                    formSubmitCheck.getSameNameList().stream().map(MongoSameNameVO::getCurrentValue)
                            .collect(Collectors.toList());
            List<Object> labelList = formSubmitCheck.getSameNameList().stream().map(MongoSameNameVO::getLabel)
                    .collect(Collectors.toList());
            Object[] objArray = {StringUtils.join(labelList, "，"), StringUtils.join(currentValueList, "，")};
            throw new ServiceException(ServiceResultCode.DATA_SAME_EXIST, objArray);
        }
    }

    @Override
    @MultiTransactional(value = {"mybatisTransactionManager", "mongoTransactionManager"})
    public void updateData(FormUpdateDataRequest formUpdateDataRequest, Boolean onlyUpdate) {
        update(formUpdateDataRequest, onlyUpdate);
    }

    @Override
    public void updateDataTrigger(FormUpdateDataRequest formUpdateDataRequest, Boolean onlyUpdate) {
        formUpdateDataRequest.setNeedWriteFlowable(Boolean.FALSE);
        update(formUpdateDataRequest, onlyUpdate);
    }

    private void startAudit(FormWorkflowCreateTaskRequest formWorkflowCreateTask, String uuid, String state,
                            FormVO info) {
        if (StringUtils.isNotEmpty(info.getSourceId())) {
            formWorkflowCreateTask.setFormId(info.getSourceId());
            formWorkflowCreateTask.setCreateFormId(info.getId());
        } else {
            formWorkflowCreateTask.setFormId(info.getId());
            formWorkflowCreateTask.setCreateFormId(info.getId());
        }
        formWorkflowCreateTask.setUuid(uuid);
        if (FormDataStatusEnum.PASS.name().equals(state) &&
                ApplicationCategoryCategoryTypeEnum.getFlowerFormType().contains(info.getFormType())) {
            formWorkflowService.createTask(formWorkflowCreateTask);
        }
    }

    public void update(FormUpdateDataRequest formUpdateDataRequest, Boolean onlyUpdate) {
        FormVO info = formService.info(formUpdateDataRequest.getFormId(), formUpdateDataRequest.getApplicationId());
        if (StringUtils.isNotEmpty(info.getSourceId())) {
            info = formService.info(info.getSourceId(), info.getApplicationId());
        }
        LowcodeDataDomain previous = info(formUpdateDataRequest.getUuid(), info.getId(), info.getApplicationId());
        // 修改部分字段
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(previous.getInstValue());
        jsonObject.putAll(formUpdateDataRequest.getInstValue());
        List<FormConfigCommon> formConfigCommonList =
                JSONArray.parseArray(JSON.parseObject(info.getConfig()).getString("body"), FormConfigCommon.class);
        buildFormData(jsonObject, info, formConfigCommonList, "update", formUpdateDataRequest.getUuid());
        // 修改数据
        LowcodeUpdateDataDomain lowcodeUpdateDataDomain = new LowcodeUpdateDataDomain();
        lowcodeUpdateDataDomain.setModifier(FormUser.getCurrent(null));
        lowcodeUpdateDataDomain.setModifyTime(new Date().getTime());
        lowcodeUpdateDataDomain.setVersion(formUpdateDataRequest.getVersion());
        lowcodeUpdateDataDomain.setInstValue(jsonObject);
        lowcodeUpdateDataDomain.setUuid(formUpdateDataRequest.getUuid());
        lowcodeUpdateDataDomain.setCollection(info.getTableName());
        if (!FormDataStatusEnum.APPROVING.name().equals(previous.getStatus())) {
            lowcodeUpdateDataDomain.setStatus(formUpdateDataRequest.getStatus());
        }
        if (FormDataStatusEnum.DRAFT.name().equals(formUpdateDataRequest.getStatus())) {
            onlyUpdate = true;
        }
        FormModelVO formModelVO =
                formModelService.info(formUpdateDataRequest.getFormId(), formUpdateDataRequest.getApplicationId());
        if (formModelVO != null && "UP".equals(formModelVO.getStatus()) && !onlyUpdate &&
                FormDataStatusEnum.DRAFT.name().equals(previous.getStatus())) {
            lowcodeUpdateDataDomain.setModelId(formModelVO.getModelId());
        }
        lowcodeUpdateDataDomain.setFormId(formUpdateDataRequest.getFormId());
        mongoDbService.updateData(lowcodeUpdateDataDomain);
        if (FormDataStatusEnum.APPROVING.name().equals(previous.getStatus()) &&
                formUpdateDataRequest.getNeedWriteFlowable()) {
            workFlowService.updateVariable(previous.getProcessInstanceId(), jsonObject);
        }
        if (!onlyUpdate && formModelVO != null && "UP".equals(formModelVO.getStatus())) {
            // 发起流程
            if (FormDataStatusEnum.DRAFT.name().equals(previous.getStatus())) {
                FormWorkflowCreateTaskRequest formWorkflowCreateTaskRequest =
                        AbstractMongoDbConverter.INSTANCE.toRequest(formUpdateDataRequest);
                formWorkflowCreateTaskRequest.setApplicationId(previous.getApplicationId());
                formWorkflowCreateTaskRequest.setCreateUserId(UserUtils.getUser().getUserId());
                formWorkflowCreateTaskRequest.setProcessInstanceId(previous.getProcessInstanceId());
                startAudit(formWorkflowCreateTaskRequest, lowcodeUpdateDataDomain.getUuid(),
                        formUpdateDataRequest.getStatus(), info);
            }
        }
        // 新增日志
        LowcodeDataDomain current = info(formUpdateDataRequest.getUuid(), info.getId(), info.getApplicationId());
        List<FormDataLogContentVO> formDataLogContentVOS =
                formDataLogService.recordLog(UserUtils.getUser(), previous, current, info,
                        formUpdateDataRequest.getStatus());
        List<String> updateKey =
                formDataLogContentVOS.stream().map(FormDataLogContentVO::getName).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(updateKey)) {
            return;
        }
        if (formUpdateDataRequest.getTriggerParentList().size() < Constants.MAX_TRIGGER_CYCLE) {
            LowcodeDataDomain lowcodeDataDomain =
                    info(formUpdateDataRequest.getUuid(), info.getId(), info.getApplicationId());
            FormDataStreamTrigger formDataStreamTrigger = new FormDataStreamTrigger();
            formDataStreamTrigger.setUpdateKey(updateKey);
            formDataStreamTrigger.setParentList(formUpdateDataRequest.getTriggerParentList());
            JSONObject instValue = lowcodeDataDomain.getInstValue();
            setPreField(formDataLogContentVOS, instValue);
            formDataStreamPublishService.trigger(formUpdateDataRequest.getFormId(), "update", lowcodeDataDomain,
                    formUpdateDataRequest.getApplicationId(), formDataStreamTrigger);
        }
    }

    @Override
    public String updateBatch(FormMongoDbBatchRequest formMongoDbBatchRequest) {
        TaskCreateRequest batchUpdate =
                new TaskCreateRequest(formMongoDbBatchRequest.getApplicationId(), formMongoDbBatchRequest.getFormId(),
                        "batchUpdate", JSONObject.toJSONString(formMongoDbBatchRequest));
        String task = taskService.createTask(batchUpdate);
        mongoTaskExecutor.execute(() -> batchUpdate(formMongoDbBatchRequest, task, UserUtils.getUser()));
        return task;
    }

    private void batchUpdate(FormMongoDbBatchRequest formMongoDbBatchRequest, String task, UserDomain user) {
        UserUtils.setUser(user);
        FormVO source =
                formService.info(formMongoDbBatchRequest.getFormId(), formMongoDbBatchRequest.getApplicationId());
        FormVO info;
        if (StringUtils.isNotEmpty(source.getSourceId())) {
            info = formService.info(source.getSourceId(), formMongoDbBatchRequest.getApplicationId());
        } else {
            info = source;
        }
        List<Criteria> criteriaList = new ArrayList<>();
        List<LowcodeDataDomain> lowcodeDataDomains = new ArrayList<>();
        if ("UUID".equals(formMongoDbBatchRequest.getUpdateType())) {
            Query query = new Query();
            MongoSearchUtils.buildCommonFilter(criteriaList, info.getApplicationId(), info.getId());
            criteriaList.add(Criteria.where("uuid").in(formMongoDbBatchRequest.getUuidList()));
            Criteria criteria = new Criteria();
            criteria.andOperator(criteriaList);
            query.addCriteria(criteria);
            lowcodeDataDomains = mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        }
        int total = lowcodeDataDomains.size();
        int success = 0;
        int fail = 0;
        for (LowcodeDataDomain previous : lowcodeDataDomains) {
            try {
                LowcodeDataDomain current =
                        JSONObject.parseObject(JSONObject.toJSONString(previous), LowcodeDataDomain.class);
                JSONObject instValue = new JSONObject();
                instValue.putAll(previous.getInstValue());
                current.setInstValue(instValue);
                for (FormMongoDbBatchRequest.UpdateField field : formMongoDbBatchRequest.getFields()) {
                    if (StringUtils.isEmpty(field.getSubForm())) {
                        instValue.put(field.getFieldId(), field.getValue());
                    } else {
                        JSONArray jsonArray = JsonObjectUtils.getJsonArray(instValue, field.getSubForm());
                        JSONArray dealArray = new JSONArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            jsonObject.put(field.getFieldId(), field.getValue());
                            dealArray.add(jsonObject);
                        }
                        instValue.put(field.getSubForm(), dealArray);
                    }
                }
                LowcodeUpdateDataDomain lowcodeUpdateDataDomain = new LowcodeUpdateDataDomain();
                lowcodeUpdateDataDomain.setInstValue(instValue);
                lowcodeUpdateDataDomain.setCollection(info.getTableName());
                lowcodeUpdateDataDomain.setUuid(previous.getUuid());
                mongoDbService.updateData(lowcodeUpdateDataDomain);
                formDataLogService.recordLog(user, previous, current, info, previous.getStatus());
                success++;
            } catch (Exception e) {
                fail++;
                log.error("数据修改失败", e);
            }
        }
        JSONObject result = new JSONObject();
        result.put("fail", fail);
        result.put("success", success);
        result.put("total", total);
        taskService.taskFinish(task, JSONObject.toJSONString(result));
        UserUtils.clearUser();
    }

    private static void setPreField(List<FormDataLogContentVO> formDataLogContentVOS, JSONObject instValue) {
        for (FormDataLogContentVO formDataLogContentVO : formDataLogContentVOS) {
            if (FormFieldTypeEnum.SUB_FORM_TYPE.getFieldType().equals(formDataLogContentVO.getType())) {
                JSONArray jsonArray = instValue.getJSONArray(formDataLogContentVO.getName());
                Map<String, FormDataLogContentSubVO> subLogMap = formDataLogContentVO.getChildren().stream()
                        .collect(Collectors.toMap(FormDataLogContentSubVO::getId, c -> c));
                JSONArray dealJson = new JSONArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject subJson = jsonArray.getJSONObject(i);
                    String id = subJson.getString("id");
                    FormDataLogContentSubVO child = subLogMap.get(id);
                    if (child != null && "修改".equals(child.getOperate())) {
                        for (FormDataLogContentVO childContent : child.getFormDataLogContentList()) {
                            subJson.put(childContent.getName() + "_pre", childContent.getPreValue());
                        }
                    }
                    dealJson.add(subJson);
                }
                instValue.put(formDataLogContentVO.getName(), dealJson);
            } else {
                instValue.put(formDataLogContentVO.getName() + "_pre", formDataLogContentVO.getPreValue());
            }
        }
    }

    @Override
    public void deleteData(String uuid, String formId, String applicationId, List<String> triggerParentList) {
        FormVO info = formService.info(formId, applicationId);
        LowcodeDataDomain exist = formMongoDbService.info(uuid, info.getId(), info.getApplicationId());
        if (StringUtils.isNotEmpty(exist.getProcessInstanceId()) &&
                FormDataStatusEnum.APPROVING.name().equals(exist.getStatus())) {
            FlowableAuditRequest flowableAuditRequest = new FlowableAuditRequest();
            flowableAuditRequest.setProcessInstanceId(exist.getProcessInstanceId());
            flowableAuditRequest.setComment("数据被删除");
            workFlowService.taskReject(flowableAuditRequest);
        }
        LowcodeUpdateStateDomain lowcodeUpdateStateDomain = new LowcodeUpdateStateDomain();
        lowcodeUpdateStateDomain.setCollection(info.getTableName());
        lowcodeUpdateStateDomain.setStatus(FormDataStatusEnum.DELETED.name());
        lowcodeUpdateStateDomain.setUuid(uuid);
        lowcodeUpdateStateDomain.setModifier(FormUser.getCurrent(null));
        lowcodeUpdateStateDomain.setModifyTime(new Date().getTime());
        mongoDbService.updateData(lowcodeUpdateStateDomain);
        if (triggerParentList.size() < Constants.MAX_TRIGGER_CYCLE) {
            FormDataStreamTrigger formDataStreamTrigger = new FormDataStreamTrigger();
            formDataStreamTrigger.setParentList(triggerParentList);
            formDataStreamPublishService.trigger(formId, "delete", exist, applicationId, formDataStreamTrigger);
        }
    }

    @Override
    public void batchDelete(FormMongoDbDeleteRequest formMongoDbDeleteRequest) {
        FormVO source =
                formService.info(formMongoDbDeleteRequest.getFormId(), formMongoDbDeleteRequest.getApplicationId());
        FormVO info;
        if (StringUtils.isEmpty(source.getSourceId())) {
            info = source;
        } else {
            info = formService.info(source.getSourceId(), formMongoDbDeleteRequest.getApplicationId());
        }
        List<LowcodeDataDomain> lowcodeDataDomains =
                mongoDbService.getByUuidList(formMongoDbDeleteRequest.getUuidList(), info.getTableName());
        long time = new Date().getTime();
        for (LowcodeDataDomain exist : lowcodeDataDomains) {
            if (StringUtils.isNotEmpty(exist.getProcessInstanceId()) &&
                    FormDataStatusEnum.APPROVING.name().equals(exist.getStatus())) {
                FlowableAuditRequest flowableAuditRequest = new FlowableAuditRequest();
                flowableAuditRequest.setProcessInstanceId(exist.getProcessInstanceId());
                flowableAuditRequest.setComment("数据被删除");
                workFlowService.taskReject(flowableAuditRequest);
            }
            LowcodeUpdateStateDomain lowcodeUpdateStateDomain = new LowcodeUpdateStateDomain();
            lowcodeUpdateStateDomain.setCollection(info.getTableName());
            lowcodeUpdateStateDomain.setStatus(FormDataStatusEnum.DELETED.name());
            lowcodeUpdateStateDomain.setUuid(exist.getUuid());
            lowcodeUpdateStateDomain.setModifier(FormUser.getCurrent(null));
            lowcodeUpdateStateDomain.setModifyTime(time);
            mongoDbService.updateData(lowcodeUpdateStateDomain);
            FormDataStreamTrigger formDataStreamTrigger = new FormDataStreamTrigger();
            formDataStreamTrigger.setParentList(new ArrayList<>());
            formDataStreamPublishService.trigger(formMongoDbDeleteRequest.getFormId(), "delete", exist,
                    formMongoDbDeleteRequest.getApplicationId(), formDataStreamTrigger);
        }
    }

    @Override
    public QueryPageVO<LowcodeDataVO> queryList(FormSearchDataRequest formSearchDataRequest) {
        FormVO info = formService.info(formSearchDataRequest.getFormId(), formSearchDataRequest.getApplicationId());
        Query query = new Query();
        buildSearchFilter(formSearchDataRequest, null, query);
        long count = mongoTemplate.count(query, info.getTableName());
        query.limit(formSearchDataRequest.getPageSize());
        query.skip((long) (formSearchDataRequest.getPageNum() - 1) * formSearchDataRequest.getPageSize());
        if (CollectionUtils.isEmpty(formSearchDataRequest.getSorts())) {
            query.with(Sort.by(Sort.Direction.DESC, "createTime"));
        } else {
            MongoSearchUtils.buildSort(query, formSearchDataRequest.getSorts());
        }
        if (CollectionUtils.isNotEmpty(formSearchDataRequest.getFields())) {
            String[] strings = MongoSearchUtils.searchField(formSearchDataRequest.getFields(), Boolean.TRUE);
            query.fields().include(strings);
        }
        // 执行查找到的匹配的全部文档信息
        List<LowcodeDataDomain> lowcodeDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        List<LowcodeDataVO> lowcodeDataList = getLowcodeDataVOS(info, lowcodeDataDomains, null, Boolean.TRUE, false);
        return new QueryPageVO<>(formSearchDataRequest.getPageNum(), formSearchDataRequest.getPageSize(), (int) count,
                lowcodeDataList);
    }

    @Override
    public QueryPageVO<LowcodeDataDomain> queryListDomain(FormSearchDataRequest formSearchDataRequest) {
        FormVO info = formService.info(formSearchDataRequest.getFormId(), formSearchDataRequest.getApplicationId());
        Query query = new Query();
        buildSearchFilter(formSearchDataRequest, null, query);
        long count = mongoTemplate.count(query, info.getTableName());
        query.limit(formSearchDataRequest.getPageSize());
        query.skip((long) (formSearchDataRequest.getPageNum() - 1) * formSearchDataRequest.getPageSize());
        if (CollectionUtils.isEmpty(formSearchDataRequest.getSorts())) {
            query.with(Sort.by(Sort.Direction.DESC, "createTime"));
        } else {
            MongoSearchUtils.buildSort(query, formSearchDataRequest.getSorts());
        }
        if (CollectionUtils.isNotEmpty(formSearchDataRequest.getFields())) {
            String[] strings = MongoSearchUtils.searchField(formSearchDataRequest.getFields(), Boolean.TRUE);
            query.fields().include(strings);
        }
        // 执行查找到的匹配的全部文档信息
        List<LowcodeDataDomain> lowcodeDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        return new QueryPageVO<>(formSearchDataRequest.getPageNum(), formSearchDataRequest.getPageSize(), (int) count,
                lowcodeDataDomains);
    }

    @Override
    public LowcodeDataVO infoAll(FormSearchDataRequest formSearchDataRequest) {
        FormPrivilegeVO formPrivilegeVO = formPrivilegeService.detail(formSearchDataRequest.getGroupId());

        FormVO info = formService.info(formSearchDataRequest.getFormId(), formSearchDataRequest.getApplicationId());
        FormVO userForm = info;
        if (StringUtils.isNotEmpty(info.getSourceId())) {
            info = formService.info(info.getSourceId(), formSearchDataRequest.getApplicationId());
        }
        LowcodeDataDomain lowcodeDataDomain =
                info(formSearchDataRequest.getUuid(), info.getId(), formSearchDataRequest.getApplicationId());
        List<LowcodeDataVO> lowcodeDataList = getLowcodeDataVOS(userForm, Collections.singletonList(lowcodeDataDomain),
                Collections.singletonList(formPrivilegeVO), Boolean.TRUE, true);
        return lowcodeDataList.get(0);
    }

    @Override
    public QueryPageVO<LowcodeDataVO> queryListLink(FormSearchDataRequest formSearchDataRequest) {
        List<AggregationOperation> aggregationList = new ArrayList<>();
        FormMongoDbLinkRequest formMongoDbLinkRequest = getFormMongoDbLinkRequest(formSearchDataRequest);
        String tableName = buildAggregate(formMongoDbLinkRequest, aggregationList);
        int rowNum = getAggregateCountSize(aggregationList, tableName);
        MongoSearchUtils.aggregationAddLimit(aggregationList, formSearchDataRequest.getPageSize(),
                formSearchDataRequest.getOffSet());
        MongoSearchUtils.addSortAgg(formSearchDataRequest.getSorts(), aggregationList);
        if (formSearchDataRequest.getFormId().startsWith(Constants.AGGREGATE_TABLE)) {
            List<LowcodeDataDomain> lowcodeInsertDataDomains =
                    mongoTemplate.aggregate(Aggregation.newAggregation(aggregationList), tableName,
                            LowcodeDataDomain.class).getMappedResults();
            List<LowcodeDataVO> lowcodeDataList = getLowcodeDataVOS(formSearchDataRequest, lowcodeInsertDataDomains);
            QueryPageVO<LowcodeDataVO> lowcodeDataVOQueryPageVO =
                    new QueryPageVO<>(formSearchDataRequest.getPageNum(), formSearchDataRequest.getPageSize(), rowNum,
                            lowcodeDataList);
            Map<String, Object> otherData = new HashMap<>();
            List<FormConfigCommon> fields =
                    formService.getFormConfigCommonList(formSearchDataRequest.getFormId(), Boolean.FALSE,
                            formSearchDataRequest.getApplicationId()).getFields();
            otherData.put("field", fields);
            lowcodeDataVOQueryPageVO.setOtherData(otherData);
            return lowcodeDataVOQueryPageVO;
        } else if (formMongoDbLinkRequest.getFormId().startsWith(Constants.FAC_PREFIX)) {
            FormDataFactoryParamVO factoryParam =
                    formDataFactoryExecuteApi.getParam(formMongoDbLinkRequest.getApplicationId(),
                            formMongoDbLinkRequest.getFormId());
            List<Document> document = MongoFunctionUtils.toDocument(aggregationList);
            AggregateIterable<JSONObject> aggregate =
                    mongoTemplate.getCollection(tableName).aggregate(document, JSONObject.class);
            List<JSONObject> jsonObjectList = new ArrayList<>();
            Iterator<JSONObject> iterator = aggregate.iterator();
            while (iterator.hasNext()) {
                jsonObjectList.add(new JSONObject(iterator.next()));
            }
            List<LowcodeDataVO> returnData = getReturnData(factoryParam, jsonObjectList, true);
            QueryPageVO<LowcodeDataVO> lowcodeDataVOQueryPageVO =
                    new QueryPageVO<>(formSearchDataRequest.getPageNum(), formSearchDataRequest.getPageSize(), rowNum,
                            returnData);
            List<FormConfigCommon> formConfigCommonList =
                    factoryParam.getFields().stream().map(AbstractFormConfigCommonConverter.INSTANCE::toConfig)
                            .collect(Collectors.toList());
            Map<String, Object> otherData = new HashMap<>();
            otherData.put("field", formConfigCommonList);
            lowcodeDataVOQueryPageVO.setOtherData(otherData);
            return lowcodeDataVOQueryPageVO;
        } else {
            List<LowcodeDataDomain> lowcodeInsertDataDomains =
                    mongoTemplate.aggregate(Aggregation.newAggregation(aggregationList), tableName,
                            LowcodeDataDomain.class).getMappedResults();
            FormVO info =
                    formService.info(formMongoDbLinkRequest.getFormId(), formMongoDbLinkRequest.getApplicationId());
            List<LowcodeDataVO> lowcodeDataList =
                    getLowcodeDataVOS(info, lowcodeInsertDataDomains, null, Boolean.FALSE, false);
            return new QueryPageVO<>(formSearchDataRequest.getPageNum(), formSearchDataRequest.getPageSize(), rowNum,
                    lowcodeDataList);
        }
    }


    private static FormMongoDbLinkRequest getFormMongoDbLinkRequest(FormSearchDataRequest formSearchDataRequest) {
        FormMongoDbLinkRequest formMongoDbLinkRequest = new FormMongoDbLinkRequest();
        formMongoDbLinkRequest.setFormId(formSearchDataRequest.getFormId());
        formMongoDbLinkRequest.setApplicationId(formSearchDataRequest.getApplicationId());
        formMongoDbLinkRequest.setFilter(formSearchDataRequest.getFilter());
        formMongoDbLinkRequest.setSorts(formSearchDataRequest.getSorts());
        formMongoDbLinkRequest.setKeyList(formSearchDataRequest.getKeyList());
        formMongoDbLinkRequest.setKeyword(formSearchDataRequest.getKeyword());
        return formMongoDbLinkRequest;
    }

    private List<LowcodeDataVO> getReturnData(FormDataFactoryParamVO factoryParam, List<JSONObject> mappedResults,
                                              Boolean needSave) {
        Map<Long, String> deptIdToMapMap = departmentService.allDeptWithDelete(UserUtils.getUser().getCompanyId());
        Map<Long, UserCompanyVO> userIdMap = userCompanyService.getAllUserWithDelete();
        List<LowcodeDataVO> lowcodeDataVOList = new ArrayList<>();
        for (JSONObject jsonObject : mappedResults) {
            LowcodeDataVO lowcodeDataVO = new LowcodeDataVO();
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            for (DataFactoryReturnFieldCommonVO returnField : factoryParam.getFields()) {
                Object object = instValue.get(returnField.getAliasName());
                if (object == null) {
                    continue;
                }
                if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType().equals(returnField.getFieldType())) {
                    FormUser formUser = new FormUser();
                    formUser.setAssigneeId(Long.valueOf(object.toString()));
                    UserCompanyVO userCompanyVO = userIdMap.get(formUser.getAssigneeId());
                    if (userCompanyVO != null) {
                        formUser.setAssigneeName(userCompanyVO.getNickName());
                    }
                    if (needSave) {
                        instValue.put(returnField.getAliasName(), Collections.singletonList(formUser));
                    } else {
                        instValue.put(returnField.getAliasName(), formUser);
                    }
                } else if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType()
                        .equals(returnField.getFieldType())) {
                    FormDept formDept = new FormDept();
                    formDept.setValue(Long.valueOf(object.toString()));
                    String deptName = deptIdToMapMap.get(formDept.getValue());
                    formDept.setLabel(deptName);
                    if (needSave) {
                        instValue.put(returnField.getAliasName(), Collections.singletonList(formDept));
                    } else {
                        instValue.put(returnField.getAliasName(), formDept);
                    }
                }
            }
            lowcodeDataVO.setUuid(Md5Utils.md5(JSONObject.toJSONString(instValue)));
            lowcodeDataVO.setInstValue(instValue);
            lowcodeDataVOList.add(lowcodeDataVO);
        }
        return lowcodeDataVOList;
    }

    private List<LowcodeDataVO> getLowcodeDataVOS(FormSearchDataRequest formSearchDataRequest,
                                                  List<LowcodeDataDomain> lowcodeInsertDataDomains) {
        List<LowcodeDataVO> lowcodeDataList = new ArrayList<>();
        FieldExistNameVO fields = formAggregateService.getFields(formSearchDataRequest.getFormId(),
                formSearchDataRequest.getApplicationId());
        List<Long> userIdList = new ArrayList<>();
        List<Long> deptIdList = new ArrayList<>();
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeInsertDataDomains) {
            for (FormConfigCommon formConfigCommon : fields.getFields()) {
                if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType().equals(formConfigCommon.getType())) {
                    Long longValue = lowcodeDataDomain.getInstValue().getLong(formConfigCommon.getName());
                    if (longValue != null) {
                        userIdList.add(longValue);
                    }
                }
                if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType().equals(formConfigCommon.getType())) {
                    Long longValue = lowcodeDataDomain.getInstValue().getLong(formConfigCommon.getName());
                    if (longValue != null) {
                        deptIdList.add(longValue);
                    }
                }
            }
        }
        Map<Long, String> idToNameMap = userService.getIdToNameMap(userIdList);
        Map<Long, String> deptedIdToMap = departmentService.deptIdToMap(deptIdList);
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeInsertDataDomains) {
            LowcodeDataVO lowcodeDataVO = AbstractFormMongoDbConverter.INSTANCE.toVO(lowcodeDataDomain);
            for (FormConfigCommon formConfigCommon : fields.getFields()) {
                if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType().equals(formConfigCommon.getType())) {
                    Long longValue = lowcodeDataVO.getInstValue().getLong(formConfigCommon.getName());
                    if (longValue != null) {
                        FormUser formUser = new FormUser();
                        formUser.setAssigneeId(longValue);
                        formUser.setAssigneeName(idToNameMap.get(longValue));
                        lowcodeDataVO.getInstValue()
                                .put(formConfigCommon.getName(), Collections.singletonList(formUser));
                    }
                }

                if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType().equals(formConfigCommon.getType())) {
                    Long longValue = lowcodeDataVO.getInstValue().getLong(formConfigCommon.getName());
                    if (longValue != null) {
                        FormDept formDept = new FormDept();
                        formDept.setValue(longValue);
                        formDept.setLabel(deptedIdToMap.get(longValue));
                        lowcodeDataVO.getInstValue()
                                .put(formConfigCommon.getName(), Collections.singletonList(formDept));
                    }
                }
            }
            lowcodeDataList.add(lowcodeDataVO);
        }
        return lowcodeDataList;
    }

    @Override
    public LowcodeDataDomain info(String uuid, String formId, String applicationId) {
        FormVO info = formService.info(formId, applicationId);
        if (StringUtils.isNotEmpty(info.getSourceId())) {
            info = formService.info(info.getSourceId(), applicationId);
        }
        return mongoDbService.getByUuid(uuid, info.getTableName());
    }

    @Override
    public LowcodeDataDomain infoByProcessInstanceId(String formId, String processInstanceId, String applicationId) {
        FormVO info = formService.info(formId, applicationId);
        return mongoDbService.getByInstanceId(processInstanceId, info.getTableName());
    }

    @Override
    public void dataStreamTriggerAgain(DataStreamTriggerLogDomain dataStreamTriggerLogDomain, String uuid) {
        String formId = dataStreamTriggerLogDomain.getFormId();
        String applicationId = dataStreamTriggerLogDomain.getApplicationId();
        FormDataLogVO formDataLogVO = formDataLogService.lastLog(uuid, applicationId);
        LowcodeDataDomain latestData = info(uuid, formId, applicationId);
        FormDataStreamTrigger formDataStreamTrigger = new FormDataStreamTrigger();
        if ("update".equals(formDataLogVO.getLogAction())) {
            setPreField(formDataLogVO.getList(), latestData.getInstValue());
            List<String> updateKey =
                    formDataLogVO.getList().stream().map(FormDataLogContentVO::getName).collect(Collectors.toList());
            formDataStreamTrigger.setUpdateKey(updateKey);
        }
        FormDataStreamTrigger trigger = new FormDataStreamTrigger();
        trigger.setParentList(new ArrayList<>());
        formDataStreamPublishService.trigger(dataStreamTriggerLogDomain.getFormId(),
                dataStreamTriggerLogDomain.getAction(), latestData, applicationId, formDataStreamTrigger,
                dataStreamTriggerLogDomain.getDataStreamId());
        Criteria criteria = Criteria.where(Constants.UUID).is(dataStreamTriggerLogDomain.getUuid());
        Query query = new Query(criteria);
        Update update = new Update();
        update.set("result", "retry_success");
        mongoTemplate.upsert(query, update, "data_stream_trigger_log");
    }

    @Override
    public QueryPageVO<LowcodeDataVO> queryListPrivilege(FormSearchDataRequest formSearchDataRequest) {
        filterCheckAndSearchValue(formSearchDataRequest.getFilter(), formSearchDataRequest.getApplicationId(),
                formSearchDataRequest.getFormId());
        FormVO info = formService.info(formSearchDataRequest.getFormId(), formSearchDataRequest.getApplicationId());
        List<AggregationOperation> aggregationOperationList = new ArrayList<>();

        SearchFilterVO searchFilterVO = buildSearchFilter(formSearchDataRequest);
        // lookup(formSearchDataRequest, mongoGroupLookUpRequestList, aggregationOperationList,
        //         formSearchDataRequest.getApplicationId());
        if (searchFilterVO.getSearch() != null) {
            aggregationOperationList.add(Aggregation.match(searchFilterVO.getSearch()));
        }
        if (CollectionUtils.isEmpty(formSearchDataRequest.getSorts())) {
            aggregationOperationList.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "createTime")));
        } else {
            MongoSearchUtils.addSortAgg(formSearchDataRequest.getSorts(), aggregationOperationList);
        }
        int count = getAggregateCountSize(aggregationOperationList, info.getTableName());
        int limit = formSearchDataRequest.getOffSet() + formSearchDataRequest.getPageSize();
        MongoSearchUtils.addLimit(aggregationOperationList, limit, formSearchDataRequest.getOffSet());
        if (CollectionUtils.isNotEmpty(formSearchDataRequest.getFields())) {
            String[] projects = MongoSearchUtils.searchField(formSearchDataRequest.getFields(), Boolean.TRUE);
            ProjectionOperation project = Aggregation.project(projects);
            aggregationOperationList.add(project);
        }
        // 执行查找到的匹配的全部文档信息
        AggregationResults<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperationList), info.getTableName(),
                        LowcodeDataDomain.class);
        List<LowcodeDataVO> lowcodeDataList = getLowcodeDataVOS(info, lowcodeInsertDataDomains.getMappedResults(),
                searchFilterVO.getFormPrivilegeList(), Boolean.TRUE, false);
        return new QueryPageVO<>(formSearchDataRequest.getPageNum(), formSearchDataRequest.getPageSize(), count,
                lowcodeDataList);
    }

    private void lookup(FormSearchDataRequest formSearchDataRequest,
                        List<MongoGroupLookUpRequest> mongoGroupLookUpRequestList,
                        List<AggregationOperation> aggregationOperations, String applicationId) {
        if (CollectionUtils.isNotEmpty(mongoGroupLookUpRequestList)) {
            List<String> groupIdList = mongoGroupLookUpRequestList.stream().map(MongoGroupLookUpRequest::getGroupId)
                    .collect(Collectors.toList());
            List<FormPrivilegeVO> formPrivilegeVOS =
                    formPrivilegeService.getByIdList(groupIdList, formSearchDataRequest.getApplicationId());
            Map<String, FormPrivilegeVO> groupIdMap =
                    formPrivilegeVOS.stream().collect(Collectors.toMap(FormPrivilegeVO::getId, c -> c));
            List<String> formIdList = mongoGroupLookUpRequestList.stream().map(MongoGroupLookUpRequest::getFormId)
                    .collect(Collectors.toList());
            List<FormVO> formVOList = formService.getByIdList(formIdList, applicationId);
            Map<String, String> formIdMap =
                    formVOList.stream().collect(Collectors.toMap(FormVO::getId, FormVO::getTableName));
            for (MongoGroupLookUpRequest mongoGroupLookUpRequest : mongoGroupLookUpRequestList) {
                FormPrivilegeVO lookupPrivilege = groupIdMap.get(mongoGroupLookUpRequest.getGroupId());
                DataFactoryRelationRequest dataFactoryRelationRequest = new DataFactoryRelationRequest();
                dataFactoryRelationRequest.setLeftField(mongoGroupLookUpRequest.getFieldId());
                dataFactoryRelationRequest.setLeftFieldType(mongoGroupLookUpRequest.getFieldType());
                dataFactoryRelationRequest.setAliasLeftField(mongoGroupLookUpRequest.getFieldId());
                dataFactoryRelationRequest.setRightField(mongoGroupLookUpRequest.getQuoteFieldId());
                dataFactoryRelationRequest.setAliasRightField(
                        MongoSearchUtils.getFieldId(mongoGroupLookUpRequest.getQuoteFieldId(),
                                mongoGroupLookUpRequest.getFieldType()));
                dataFactoryRelationRequest.setRightFieldType(mongoGroupLookUpRequest.getQuoteFieldType());
                List<Criteria> criteriaList = new ArrayList<>();
                MongoSearchUtils.buildCommonFilter(criteriaList, formSearchDataRequest.getApplicationId(),
                        mongoGroupLookUpRequest.getFormId());
                List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList =
                        FormPrivilegeUtils.getDataScope(Collections.singletonList(lookupPrivilege));
                buildMongodbDataScope(criteriaList, formPrivilegeDataScopeDomainList, new ArrayList<>(),
                        formSearchDataRequest.getApplicationId(), formSearchDataRequest.getFormId());
                List<Document> documentList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(criteriaList)) {
                    Criteria search = new Criteria();
                    search.andOperator(criteriaList);
                    MatchOperation match = Aggregation.match(search);
                    documentList.addAll(match.toPipelineStages(Aggregation.DEFAULT_CONTEXT));
                }
                List<DataFactoryRelationRequest> relation = Collections.singletonList(dataFactoryRelationRequest);
                documentList.add(MongoFunctionUtils.pipeline(relation));
                LookupAggregation lookupAggregation =
                        new LookupAggregation(formIdMap.get(mongoGroupLookUpRequest.getFormId()), documentList,
                                MongoFunctionUtils.let(relation), mongoGroupLookUpRequest.getAlias());
                aggregationOperations.add(lookupAggregation);
            }
        }
    }

    @Override
    public List<FormExtraFunctionButton> infoButton(String applicationId, String formId, String id, String groupId) {
        FormVO info = formService.info(formId, applicationId);
        if (StringUtils.isNotEmpty(info.getSourceId())) {
            info = formService.info(info.getSourceId(), applicationId);
        }
        LowcodeDataDomain lowcodeDataDomain = info(id, info.getId(), applicationId);
        FormPrivilegeVO formPrivilegeVO = formPrivilegeService.detail(groupId);
        List<FormExtraFunctionVO> formExtraFunctionVOList = new ArrayList<>();
        if (formPrivilegeVO != null) {
            formExtraFunctionVOList = formExtraFunctionServiceImpl.getByFormIdAndGroupId(applicationId, formId,
                    Collections.singletonList(formPrivilegeVO.getId()));
        }
        LowcodeDataVO lowcodeDataVO = AbstractFormMongoDbConverter.INSTANCE.toVO(lowcodeDataDomain);
        return FormPrivilegeUtils.getButton(lowcodeDataVO, formExtraFunctionVOList,
                FormExtraFunctionLocationEnum.INFO.name());
    }

    @Override
    public FormMongoDbLinkVO link(FormMongoDbLinkRequest formMongoDbLinkRequest) {
        List<AggregationOperation> aggregationList = new ArrayList<>();
        String tableName = buildAggregate(formMongoDbLinkRequest, aggregationList);
        List<Document> document = MongoFunctionUtils.toDocument(aggregationList);
        AggregateIterable<JSONObject> aggregate =
                mongoTemplate.getCollection(tableName).aggregate(document, JSONObject.class);
        List<LowcodeDataDomain> lowcodeDataDomainList = new ArrayList<>();
        Iterator<JSONObject> iterator = aggregate.iterator();
        while (iterator.hasNext()) {
            lowcodeDataDomainList.add(
                    JSONObject.parseObject(new JSONObject(iterator.next()).toJSONString(), LowcodeDataDomain.class));
        }
        if (CollectionUtils.isEmpty(lowcodeDataDomainList)) {
            return new FormMongoDbLinkVO();
        }
        LowcodeDataDomain lowcodeDataDomain = lowcodeDataDomainList.get(0);
        FormMongoDbLinkVO formMongoDbLinkVO = new FormMongoDbLinkVO();
        JSONObject returnJson = getReturnJson(formMongoDbLinkRequest, lowcodeDataDomain);
        formMongoDbLinkVO.setInstValue(returnJson);
        return formMongoDbLinkVO;
    }

    private String buildAggregate(FormMongoDbLinkRequest formMongoDbLinkRequest,
                                  List<AggregationOperation> aggregationList) {
        List<Criteria> criteriaList = new ArrayList<>();
        // 高级搜索
        String tableName;
        if (formMongoDbLinkRequest.getFormId().startsWith(Constants.AGGREGATE_TABLE)) {
            FormAggregateMongoVO formAggregateMongoVO =
                    formAggregateService.buildAggregate(formMongoDbLinkRequest.getFormId(),
                            formMongoDbLinkRequest.getApplicationId());
            tableName = formAggregateMongoVO.getTableName();
            aggregationList.addAll(formAggregateMongoVO.getAggregationList());
        } else if (formMongoDbLinkRequest.getFormId().startsWith(Constants.FAC_PREFIX)) {
            FormDataFactoryParamVO factoryParam =
                    formDataFactoryExecuteApi.getParam(formMongoDbLinkRequest.getApplicationId(),
                            formMongoDbLinkRequest.getFormId());
            aggregationList.addAll(factoryParam.getAggregationOperations());
            tableName = factoryParam.getTableName();
        } else {
            if (CollectionUtils.isEmpty(formMongoDbLinkRequest.getSorts())) {
                aggregationList.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "createTime")));
            }
            MongoSearchUtils.buildCommonFilter(criteriaList, formMongoDbLinkRequest.getApplicationId(),
                    formMongoDbLinkRequest.getFormId());
            FormVO viewForm =
                    formService.info(formMongoDbLinkRequest.getFormId(), formMongoDbLinkRequest.getApplicationId());
            FormVO info;
            if (StringUtils.isNotEmpty(viewForm.getSourceId())) {
                info = formService.info(viewForm.getSourceId(), formMongoDbLinkRequest.getApplicationId());
            } else {
                info = viewForm;
            }
            tableName = info.getTableName();
        }
        Criteria criteria =
                MongoSearchUtils.buildCriteriaByFilter(formMongoDbLinkRequest.getFilter(), new ArrayList<>());
        if (criteria != null) {
            criteriaList.add(criteria);
        }
        FormSearchDataRequest formSearchDataRequest = new FormSearchDataRequest();
        formSearchDataRequest.setKeyList(formMongoDbLinkRequest.getKeyList());
        formSearchDataRequest.setKeyword(formMongoDbLinkRequest.getKeyword());
        keywordSearch(formSearchDataRequest, criteriaList);
        MongoSearchUtils.addMatch(criteriaList, aggregationList);
        if (CollectionUtils.isNotEmpty(formMongoDbLinkRequest.getSorts())) {
            MongoSearchUtils.addSortAgg(formMongoDbLinkRequest.getSorts(), aggregationList);
        }
        return tableName;
    }

    private static JSONObject getReturnJson(FormMongoDbLinkRequest formMongoDbLinkRequest,
                                            LowcodeDataDomain lowcodeDataDomain) {
        JSONObject instValue = lowcodeDataDomain.getInstValue();
        if (instValue == null) {
            return new JSONObject();
        }
        JSONObject returnJson = new JSONObject();
        if (CollectionUtils.isEmpty(formMongoDbLinkRequest.getChildFieldList())) {
            for (String field : formMongoDbLinkRequest.getFieldList()) {
                Object value = instValue.get(field);
                if (value != null) {
                    if (value instanceof ArrayList || value instanceof LinkedHashMap) {
                        returnJson.put(field, value);
                    } else {
                        Object dealValue = MongoDataUtils.decryptAndEncryptReturn(value);
                        returnJson.put(field, dealValue);
                    }
                }
            }
        } else {
            JSONArray jsonArray = instValue.getJSONArray(formMongoDbLinkRequest.getFieldList().get(0));
            JSONArray returnArray = new JSONArray();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject subReturnJson = new JSONObject();
                    for (String field : formMongoDbLinkRequest.getChildFieldList()) {
                        Object value = jsonObject.get(field);
                        if (value != null) {
                            Object dealValue = MongoDataUtils.decryptAndEncryptReturn(value);
                            subReturnJson.put(field, dealValue);
                        }
                    }
                    returnArray.add(subReturnJson);
                }
            }
            returnJson.put(formMongoDbLinkRequest.getFieldList().get(0), returnArray);
        }
        return returnJson;
    }

    @Override
    public FormMongoDbLinkVO linkList(FormMongoDbLinkRequest formMongoDbLinkRequest) {
        List<AggregationOperation> aggregationList = new ArrayList<>();
        String tableName = buildAggregate(formMongoDbLinkRequest, aggregationList);
        List<Document> document = MongoFunctionUtils.toDocument(aggregationList);
        AggregateIterable<JSONObject> aggregate =
                mongoTemplate.getCollection(tableName).aggregate(document, JSONObject.class);
        List<LowcodeDataDomain> lowcodeDataDomainList = new ArrayList<>();
        Iterator<JSONObject> iterator = aggregate.iterator();
        while (iterator.hasNext()) {
            lowcodeDataDomainList.add(
                    JSONObject.parseObject(new JSONObject(iterator.next()).toJSONString(), LowcodeDataDomain.class));
        }
        if (CollectionUtils.isEmpty(lowcodeDataDomainList)) {
            return new FormMongoDbLinkVO();
        }
        FormMongoDbLinkVO formMongoDbLinkVO = new FormMongoDbLinkVO();
        List<JSONObject> instValueList = new ArrayList<>();
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomainList) {
            JSONObject returnJson = getReturnJson(formMongoDbLinkRequest, lowcodeDataDomain);
            instValueList.add(returnJson);
        }
        String jsonString = JSONObject.toJSONString(instValueList);
        instValueList =
                JSONArray.parseArray(jsonString, JSONObject.class).stream().distinct().collect(Collectors.toList());
        formMongoDbLinkVO.setInstValueList(instValueList);
        return formMongoDbLinkVO;
    }

    @Override
    public List<Object> linkSelect(FormMongodbLinkSelectRequest formMongodbLinkSelectRequest) {
        List<AggregationOperation> aggregationList = new ArrayList<>();
        FormMongoDbLinkRequest formMongoDbLinkRequest = new FormMongoDbLinkRequest();
        formMongoDbLinkRequest.setFormId(formMongodbLinkSelectRequest.getFormId());
        formMongoDbLinkRequest.setApplicationId(formMongodbLinkSelectRequest.getApplicationId());
        formMongoDbLinkRequest.setFilter(formMongodbLinkSelectRequest.getFilter());
        formMongoDbLinkRequest.setSorts(formMongodbLinkSelectRequest.getSorts());
        String tableName = buildAggregate(formMongoDbLinkRequest, aggregationList);
        List<Document> document = MongoFunctionUtils.toDocument(aggregationList);
        AggregateIterable<JSONObject> aggregate =
                mongoTemplate.getCollection(tableName).aggregate(document, JSONObject.class);
        List<LowcodeDataDomain> lowcodeDataDomainList = new ArrayList<>();
        Iterator<JSONObject> iterator = aggregate.iterator();
        while (iterator.hasNext()) {
            lowcodeDataDomainList.add(
                    JSONObject.parseObject(new JSONObject(iterator.next()).toJSONString(), LowcodeDataDomain.class));
        }
        return getLinkSelectData(formMongodbLinkSelectRequest, lowcodeDataDomainList);
    }

    @Override
    public Boolean checkDataExist(String uuid, MongodbSearchFilter mongodbSearchFilter, String formId,
                                  String applicationId) {
        FormVO info = formService.info(formId, applicationId);
        Query query = new Query();
        // 高级搜索
        MongoSearchUtils.buildSearchCondition(query, mongodbSearchFilter);
        Criteria criteria = Criteria.where("uuid").is(uuid);
        query.addCriteria(criteria);
        List<LowcodeDataDomain> lowcodeDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        return CollectionUtils.isNotEmpty(lowcodeDataDomains);
    }

    @Override
    public void copyData(String formConfig, String tableName, String toTableName, Boolean needTrans, Boolean exist,
                         String applicationId, String formId, String generaTemplateId) {
        if (exist && !generaTemplateId.equals(toTableName)) {
            mongoTemplate.dropCollection(toTableName);
        }
        Query query = new Query();
        if (!applicationId.equals(tableName)) {
            MongoSearchUtils.buildCommonFilterWithout(query, applicationId, formId);
        } else {
            MongoSearchUtils.buildCommonFilter(query, applicationId, formId);
        }
        List<LowcodeDataDomain> lowcodeDataDomains = mongoTemplate.find(query, LowcodeDataDomain.class, tableName);
        List<FormConfigCommon> formConfigCommonList = FormConfigUtils.getConfigList(formConfig, "", Boolean.FALSE);
        FormUser current = FormUser.getCurrent(UserUtils.getUser());
        List<FormDept> currentDept = FormDept.getCurrentDept(UserUtils.getUser());
        if (CollectionUtils.isEmpty(currentDept)) {
            FormDept formDept = new FormDept();
            formDept.setValue(0L);
            formDept.setLabel(companyService.info(UserUtils.getUser().getCompanyId()).getCompanyName());
            currentDept.add(formDept);
        }
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomains) {
            lowcodeDataDomain.setCreator(current);
            lowcodeDataDomain.setModifier(current);
            lowcodeDataDomain.setApplicationId(generaTemplateId);
            lowcodeDataDomain.setDeptList(currentDept);
            lowcodeDataDomain.setProcessInstanceId(null);
            lowcodeDataDomain.setParentInfo(null);
            lowcodeDataDomain.setStatus(FormDataStatusEnum.PASS.name());
            lowcodeDataDomain.setUuid(ObjectId.getGuid());
            lowcodeDataDomain.setCompanyId(UserUtils.getUser().getCompanyId());
            if (needTrans) {
                for (FormConfigCommon formConfigCommon : formConfigCommonList) {
                    FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
                    if (formDataService != null) {
                        formDataService.dealWhileUseTemplate(lowcodeDataDomain.getInstValue(), formConfigCommon,
                                current, currentDept, true);
                    }
                }
            } else {
                for (FormConfigCommon formConfigCommon : formConfigCommonList) {
                    FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
                    if (formDataService != null) {
                        formDataService.dealWhileUseTemplate(lowcodeDataDomain.getInstValue(), formConfigCommon,
                                current, currentDept, false);
                    }
                }
            }
        }
        mongoTemplate.insert(lowcodeDataDomains, toTableName);
    }

    @Override
    public Boolean checkUserFilled(MongoDbUserFilledRequest mongoDbUserFilledRequest) {
        FormVO info =
                formService.info(mongoDbUserFilledRequest.getFormId(), mongoDbUserFilledRequest.getApplicationId());
        Query query = new Query();
        MongoSearchUtils.buildCommonFilter(query, mongoDbUserFilledRequest.getApplicationId(),
                mongoDbUserFilledRequest.getFormId());
        Criteria criteria = Criteria.where("publicUserSign").is(mongoDbUserFilledRequest.getPublicUserSign());
        query.addCriteria(criteria);
        List<LowcodeDataDomain> lowcodeDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        return CollectionUtils.isNotEmpty(lowcodeDataDomains);
    }

    @Override
    public JSONObject summary(FormMongoDbSummaryRequest formMongoDbSummary) {
        if (CollectionUtils.isEmpty(formMongoDbSummary.getFields())) {
            return new JSONObject();
        }
        List<AggregationOperation> aggregationList = new ArrayList<>();
        String tableName = null;
        if (formMongoDbSummary.getFormId().startsWith(Constants.FAC_PREFIX)) {
            FormMongoDbLinkRequest formMongoDbLinkRequest = new FormMongoDbLinkRequest();
            formMongoDbLinkRequest.setFormId(formMongoDbSummary.getFormId());
            formMongoDbLinkRequest.setApplicationId(formMongoDbSummary.getApplicationId());
            formMongoDbLinkRequest.setFilter(formMongoDbSummary.getFilter());
            tableName = buildAggregate(formMongoDbLinkRequest, aggregationList);
        } else {
            FormSearchDataRequest formSearchDataRequest =
                    AbstractFormMongoDbConverter.INSTANCE.toRequest(formMongoDbSummary);
            SearchFilterVO searchFilterVO = buildSearchFilter(formSearchDataRequest);
            if (searchFilterVO.getSearch() != null) {
                aggregationList.add(Aggregation.match(searchFilterVO.getSearch()));
            }
            FormVO info;
            FormVO viewForm =
                    formService.info(formSearchDataRequest.getFormId(), formSearchDataRequest.getApplicationId());
            if (StringUtils.isNotEmpty(viewForm.getSourceId())) {
                info = formService.info(viewForm.getSourceId(), formSearchDataRequest.getApplicationId());
            } else {
                info = viewForm;
            }
            tableName = info.getTableName();
        }
        AddFieldsOperation.AddFieldsOperationBuilder addFields = Aggregation.addFields();
        Map<String, List<String>> projectFieldMap = new HashMap<>();
        for (FormMongoDbSummaryFieldRequest summaryField : formMongoDbSummary.getFields()) {
            if ("calculate".equals(summaryField.getFieldType())) {
                String fieldSubForm = summaryField.getSubForm() == null ? "" : summaryField.getSubForm();
                List<String> fields = projectFieldMap.get(fieldSubForm);
                if (CollectionUtils.isEmpty(fields)) {
                    fields = new ArrayList<>(summaryField.getQuoteFields());
                } else {
                    fields.addAll(summaryField.getQuoteFields());
                }
                projectFieldMap.put(fieldSubForm, fields);
                String fieldId = MongoSearchUtils.getFieldId(summaryField.getFieldId(), summaryField.getFieldType());
                if (StringUtils.isEmpty(summaryField.getSubForm())) {
                    addFields = addFields.addField(fieldId).withValueOfExpression(summaryField.getFormula());
                } else {
                    String subForm = MongoSearchUtils.getFieldId(summaryField.getSubForm(), "");
                    Object function = MongoFunctionUtils.getFunction(summaryField.getFormula());
                    MapOperation mapOperation = new MapOperation(subForm, summaryField.getSubForm(), function);
                    AggregationExpression aggregationExpression =
                            MongoFunctionUtils.subFormFunctionAgg(summaryField.getFieldId(), summaryField.getSubForm(),
                                    summaryField.getOp(), mapOperation.toDocument(Aggregation.DEFAULT_CONTEXT));
                    addFields = addFields.addField(fieldId).withValue(aggregationExpression);
                    continue;
                }
            }
            if (StringUtils.isEmpty(summaryField.getSubForm())) {
                if (MongodbCalculateEnum.NULL_COUNT.name().equals(summaryField.getOp())) {
                    String fieldId =
                            MongoSearchUtils.getFieldId(summaryField.getFieldId(), summaryField.getFieldType());
                    ConditionalOperators.Cond otherwise =
                            ConditionalOperators.Cond.when(MongoFunctionUtils.orExpress(fieldId)).then(1).otherwise(0);
                    addFields = addFields.addField(summaryField.getFieldId() + "_" + summaryField.getOp())
                            .withValue(otherwise);
                } else if (MongodbCalculateEnum.COUNT.name().equals(summaryField.getOp())) {
                    String fieldId =
                            MongoSearchUtils.getFieldId(summaryField.getFieldId(), summaryField.getFieldType());
                    ConditionalOperators.Cond otherwise =
                            ConditionalOperators.Cond.when(MongoFunctionUtils.orExpress(fieldId)).then(0).otherwise(1);
                    addFields = addFields.addField(summaryField.getFieldId() + "_" + summaryField.getOp())
                            .withValue(otherwise);
                }
            } else {
                AggregationExpression aggregationExpression =
                        MongoFunctionUtils.subFormAgg(summaryField.getFieldId(), summaryField.getSubForm(),
                                summaryField.getOp());
                addFields = addFields.addField(summaryField.getFieldId() + "_" + summaryField.getOp())
                        .withValue(aggregationExpression);
            }
        }
        GroupOperation groupOperation = Aggregation.group();
        for (FormMongoDbSummaryFieldRequest summaryField : formMongoDbSummary.getFields()) {
            if (StringUtils.isNotEmpty(summaryField.getSubForm()) && !"calculate".equals(summaryField.getFieldType())) {
                groupOperation = groupOperation.sum(summaryField.getFieldId() + "_" + summaryField.getOp())
                        .as(summaryField.getFieldId());
            } else {
                if (MongodbCalculateEnum.NULL_COUNT.name().equals(summaryField.getOp())) {
                    groupOperation = groupOperation.sum(summaryField.getFieldId() + "_" + summaryField.getOp())
                            .as(summaryField.getFieldId());
                } else if (MongodbCalculateEnum.COUNT.name().equals(summaryField.getOp())) {
                    groupOperation = groupOperation.sum(summaryField.getFieldId() + "_" + summaryField.getOp())
                            .as(summaryField.getFieldId());
                } else {
                    MongodbCalculateEnum mongodbCalculateEnum = MongodbCalculateEnum.valueOf(summaryField.getOp());
                    String fieldId =
                            MongoSearchUtils.getFieldId(summaryField.getFieldId(), summaryField.getFieldType());
                    switch (mongodbCalculateEnum) {
                        case AVG:
                            groupOperation = groupOperation.avg(fieldId).as(summaryField.getFieldId());
                            break;
                        case MAX:
                            groupOperation = groupOperation.max(fieldId).as(summaryField.getFieldId());
                            break;
                        case MIN:
                            groupOperation = groupOperation.min(fieldId).as(summaryField.getFieldId());
                            break;
                        case SUM:
                            groupOperation = groupOperation.sum(fieldId).as(summaryField.getFieldId());
                            break;
                    }
                }
            }
        }
        if (!projectFieldMap.isEmpty()) {
            AggregationOperation aggregationOperation = MongoSearchUtils.summaryProjectField(projectFieldMap);
            aggregationList.add(aggregationOperation);
        }
        aggregationList.add(addFields.build());
        aggregationList.add(groupOperation);
        List<Document> document = MongoFunctionUtils.toDocument(aggregationList);
        AggregateIterable<JSONObject> aggregate =
                mongoTemplate.getCollection(tableName).aggregate(document, JSONObject.class);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        Iterator<JSONObject> iterator = aggregate.iterator();
        while (iterator.hasNext()) {
            jsonObjectList.add(JSONObject.parseObject(new JSONObject(iterator.next()).toJSONString()));
        }
        if (jsonObjectList.isEmpty()) {
            JSONObject nullJson = new JSONObject();
            for (FormMongoDbSummaryFieldRequest formMongoDbSummaryFieldRequest : formMongoDbSummary.getFields()) {
                nullJson.put(formMongoDbSummaryFieldRequest.getFieldId(), null);
            }
            return nullJson;
        }
        return jsonObjectList.get(0);
    }

    @Override
    public List<LowcodeDataVO> getWorkflowData(List<FormWorkflowDataDomain> formWorkflowDataDomains) {
        if (formWorkflowDataDomains.isEmpty()) {
            return Collections.emptyList();
        }
        List<AggregationOperation> aggregationList = new ArrayList<>();
        FormWorkflowDataDomain mainDomain = formWorkflowDataDomains.get(0);
        List<Criteria> criteriaList = new ArrayList<>();
        MongoSearchUtils.buildCommonFilter(criteriaList, mainDomain.getApplicationId(), mainDomain.getFormId());
        criteriaList.add(Criteria.where("uuid").in(mainDomain.getUuids()));
        MongoSearchUtils.addMatch(criteriaList, aggregationList);
        int size = formWorkflowDataDomains.size();
        for (int i = 1; i < size; i++) {
            List<AggregationOperation> otherAggregationList = new ArrayList<>();
            FormWorkflowDataDomain formWorkflowDataDomain = formWorkflowDataDomains.get(i);
            List<Criteria> otherCriteriaList = new ArrayList<>();
            MongoSearchUtils.buildCommonFilter(otherCriteriaList, formWorkflowDataDomain.getApplicationId(),
                    formWorkflowDataDomain.getFormId());
            otherCriteriaList.add(Criteria.where("uuid").in(formWorkflowDataDomain.getUuids()));
            MongoSearchUtils.addMatch(otherCriteriaList, otherAggregationList);
            UnionWithAggregation unionWithAggregation = new UnionWithAggregation(formWorkflowDataDomain.getTableName(),
                    Aggregation.newAggregation(otherAggregationList).toPipeline(Aggregation.DEFAULT_CONTEXT));
            aggregationList.add(unionWithAggregation);
        }
        List<LowcodeDataDomain> mappedResults =
                mongoTemplate.aggregate(Aggregation.newAggregation(aggregationList), mainDomain.getTableName(),
                        LowcodeDataDomain.class).getMappedResults();
        Map<String, FormWorkflowDataDomain> keyToDomainMap = formWorkflowDataDomains.stream()
                .collect(Collectors.toMap(c -> c.getFormId() + "_" + c.getApplicationId(), c -> c));
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        List<LowcodeDataVO> lowcodeDataVOList =
                mappedResults.stream().map(AbstractFormMongoDbConverter.INSTANCE::toVO).collect(Collectors.toList());
        List<LowcodeDataVO> returnList = new ArrayList<>();
        Map<String, List<LowcodeDataVO>> keyToListMap = lowcodeDataVOList.stream()
                .collect(Collectors.groupingBy(c -> c.getFormId() + "_" + c.getApplicationId()));
        keyToListMap.forEach((key, value) -> {
            FormWorkflowDataDomain formWorkflowDataDomain = keyToDomainMap.get(key);
            List<FlowableFormFieldConfig> flowableFormFieldConfigs =
                    formWorkflowDataDomain.getFlowableFormFieldConfigs();
            for (FlowableFormFieldConfig flowableFormFieldConfig : flowableFormFieldConfigs) {
                String type = flowableFormFieldConfig.getType();
                FormDataService formDataService = formDataContext.getHandler(type);
                if (formDataService != null) {
                    FormConfigCommon formConfigCommon = new FormConfigCommon();
                    formConfigCommon.setType(type);
                    formConfigCommon.setName(flowableFormFieldConfig.getName());
                    formDataService.dealWhileReturn(value, formConfigCommon, null, systemAllData);
                }
            }
            returnList.addAll(value);
        });
        return returnList;
    }
}
