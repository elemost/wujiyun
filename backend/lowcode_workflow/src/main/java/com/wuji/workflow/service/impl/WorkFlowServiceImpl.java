package com.wuji.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.wuji.admin.components.CorpCoopComponent;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.trans.MultiTransactional;
import com.wuji.common.utils.UserUtils;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.converter.AbstractFlowableConverter;
import com.wuji.workflow.enums.FlowCommentEnum;
import com.wuji.workflow.enums.ProcessStateEnum;
import com.wuji.workflow.enums.WorkflowResultCode;
import com.wuji.workflow.exception.FlowableBizException;
import com.wuji.workflow.model.domain.AssigneeDomain;
import com.wuji.workflow.model.domain.CandidateDomain;
import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
import com.wuji.workflow.model.domain.FlowablePageQueryDomain;
import com.wuji.workflow.model.entity.HiIdentitylinkEntity;
import com.wuji.workflow.model.flowable.enums.ApprovalMultiEnum;
import com.wuji.workflow.model.info.FlowableFormOtherConfig;
import com.wuji.workflow.model.request.FlowableAuditRequest;
import com.wuji.workflow.model.request.FlowableCopyCreateRequest;
import com.wuji.workflow.model.request.FlowableDoneListRequest;
import com.wuji.workflow.model.request.FlowableOperateLogRequest;
import com.wuji.workflow.model.request.FlowableOwnerListRequest;
import com.wuji.workflow.model.request.FlowableToDoListRequest;
import com.wuji.workflow.model.vo.DoneTaskVO;
import com.wuji.workflow.model.vo.FlowableDetailVO;
import com.wuji.workflow.model.vo.FlowableOperateLogVO;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.model.vo.OwnerTaskVO;
import com.wuji.workflow.model.vo.PendingTaskVO;
import com.wuji.workflow.model.vo.ProcessNodeVO;
import com.wuji.workflow.model.vo.ProcessViewerVO;
import com.wuji.workflow.service.FlowableActivityConfigService;
import com.wuji.workflow.service.FlowableCopyService;
import com.wuji.workflow.service.FlowableOperateLogService;
import com.wuji.workflow.service.HiIdentitylinkService;
import com.wuji.workflow.service.ModelManageService;
import com.wuji.workflow.service.ReModelService;
import com.wuji.workflow.service.WorkFlowService;
import com.wuji.workflow.utils.FlowableUtils;
import com.wuji.workflow.utils.ModelUtils;
import com.wuji.workflow.utils.ProcessUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private UserService userService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private FlowableCopyService flowableCopyService;

    @Autowired
    private FlowableActivityConfigService flowableActivityConfigService;

    @Autowired
    private ModelManageService modelManageService;

    @Autowired
    private FlowableOperateLogService flowableOperateLogService;

    @Autowired
    private HiIdentitylinkService hiIdentitylinkService;

    @Autowired
    private CorpCoopComponent corpCoopComponent;

    @Autowired
    private ReModelService reModelService;

    @Override
    public String createTask(String deploymentId, String userId, HashMap<String, Object> variables) {
        identityService.setAuthenticatedUserId(userId);
        variables.put(FlowableConstant.PROCESS_INITIATOR, userId);
        variables.put(FlowableConstant.PROCESS_STATUS_SING_KEY, ProcessStateEnum.RUNNING.getStatus());
        Date date = new Date();
        ProcessDefinition processDefinition =
                repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        ProcessInstance processInstance =
                runtimeService.createProcessInstanceBuilder().processDefinitionId(processDefinition.getId())
                        .variables(variables).tenantId(UserUtils.getUser().getCompanyId().toString()).start();
        FlowableOperateLogRequest flowableOperateLogRequest = new FlowableOperateLogRequest();
        flowableOperateLogRequest.setTaskName("开始");
        flowableOperateLogRequest.setCreateTime(date);
        flowableOperateLogRequest.setProcessInstanceId(processInstance.getProcessInstanceId());
        flowableOperateLogRequest.setOperate(FlowCommentEnum.NORMAL.getType());
        flowableOperateLogRequest.setTaskCreateTime(date);
        flowableOperateLogService.saveLog(flowableOperateLogRequest);
        finishFirstAudit(processInstance.getProcessInstanceId());
        return processInstance.getProcessInstanceId();
    }

    @Override
    public void finishFirstAudit(String processInstanceId) {
        // 自动完成第一个任务
        Task autoTask = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey("first")
                .singleResult();
        if (autoTask != null) {
            FlowableOperateLogRequest save = new FlowableOperateLogRequest();
            taskService.complete(autoTask.getId());
            save.setTaskName(autoTask.getName());
            save.setCreateTime(new Date());
            save.setProcessInstanceId(processInstanceId);
            save.setOperate(FlowCommentEnum.NORMAL.getType());
            save.setTaskCreateTime(new Date());
            flowableOperateLogService.saveLog(save);
        }
    }

    @Override
    @MultiTransactional(value = {"mybatisTransactionManager", "mongoTransactionManager"})
    public void taskComplete(FlowableAuditRequest flowableAuditRequest, Boolean needAddOperate) {
        // 查询任务id 或 流程实例id
        setTaskAndProcessInstanceId(flowableAuditRequest);
        String taskId = flowableAuditRequest.getTaskId();
        Task task = taskService.createTaskQuery().taskId(taskId).includeProcessVariables().singleResult();
        // 设置流程状态为已完成
        updateStatus(flowableAuditRequest.getProcessInstanceId(), ProcessStateEnum.RUNNING, task);

        UserDomain user = UserUtils.getUser();
        if (((TaskEntityImpl) task).isDeleted()) {
            return;
        }
        if (task.getDelegationState() != null && task.getDelegationState() == DelegationState.PENDING) {
            taskService.resolveTask(taskId);
        }
        taskService.setAssignee(taskId, user.getUserId());
        taskService.complete(taskId);
        // 添加审批意见
        if (needAddOperate) {
            addCommentTask(FlowCommentEnum.NORMAL, flowableAuditRequest.getComment(), task);
        }
    }

    @Override
    @MultiTransactional(value = {"mybatisTransactionManager", "mongoTransactionManager"})
    public void taskReject(FlowableAuditRequest flowableAuditRequest) {

        String taskId = flowableAuditRequest.getTaskId();
        String processInstanceId = flowableAuditRequest.getProcessInstanceId();
        Task task = null;
        if (StringUtils.isNotEmpty(taskId)) {
            task = taskService.createTaskQuery().includeProcessVariables().taskId(taskId).singleResult();
        } else {
            List<Task> list =
                    taskService.createTaskQuery().includeProcessVariables().processInstanceId(processInstanceId).list();
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            task = list.get(0);
        }
        // 当前任务 task

        // 设置流程状态为已终结
        updateStatus(processInstanceId, ProcessStateEnum.TERMINATED, task);

        // 获取所有节点信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        EndEvent endEvent = ModelUtils.getEndEvent(bpmnModel);
        // 终止流程
        List<Execution> executions = runtimeService.createExecutionQuery().parentId(task.getProcessInstanceId()).list();
        List<String> executionIds = executions.stream().map(Execution::getId).collect(Collectors.toList());
        runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId())
                .moveExecutionsToSingleActivityId(executionIds, endEvent.getId()).changeState();
        if (StringUtils.isEmpty(taskId)) {
            addCommentInstance(flowableAuditRequest, task, FlowCommentEnum.REJECT);
        } else {
            addCommentTask(FlowCommentEnum.REJECT, flowableAuditRequest.getComment(), task);
        }
    }

    private void addCommentInstance(FlowableAuditRequest flowableAuditRequest, Task task, FlowCommentEnum reject) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(flowableAuditRequest.getProcessInstanceId()).singleResult();
        FlowableOperateLogRequest flowableOperateLogRequest = new FlowableOperateLogRequest();
        flowableOperateLogRequest.setComment(flowableAuditRequest.getComment());
        flowableOperateLogRequest.setTaskName(task.getName());
        flowableOperateLogRequest.setCreateTime(new Date());
        flowableOperateLogRequest.setProcessInstanceId(task.getProcessInstanceId());
        flowableOperateLogRequest.setOperate(reject.getType());
        flowableOperateLogRequest.setTaskCreateTime(historicProcessInstance.getStartTime());
        flowableOperateLogService.saveLog(flowableOperateLogRequest);
    }

    private void updateStatus(String processInstanceId, ProcessStateEnum processStateEnum, Task task) {
        Map<String, Object> variable = new HashMap<>();
        variable.put(FlowableConstant.PROCESS_STATUS_SING_KEY, processStateEnum.getStatus());
        variable.put(FlowableConstant.PREVIOUS_TASK_ID, task.getId());
        runtimeService.setVariables(processInstanceId, variable);
    }

    @Override
    @MultiTransactional(value = {"mybatisTransactionManager", "mongoTransactionManager"})
    public void taskReturn(FlowableAuditRequest flowableAuditRequest) {
        UserDomain user = UserUtils.getUser();
        // 查询任务id 或 流程实例id
        setTaskAndProcessInstanceId(flowableAuditRequest);
        String taskId = flowableAuditRequest.getTaskId();
        Task task = taskService.createTaskQuery().includeProcessVariables().taskId(taskId).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition =
                repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId())
                        .singleResult();
        // 获取流程模型信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        // 获取当前任务节点元素
        FlowElement source = ModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
        // 获取跳转的节点元素
        FlowElement target = ModelUtils.getFlowElementById(bpmnModel, flowableAuditRequest.getTargetKey());
        // 从当前节点向前扫描，判断当前节点与目标节点是否属于串行，若目标节点是在并行网关上或非同一路线上，不可跳转
        boolean isSequential = ModelUtils.isSequentialReachable(source, target, new HashSet<>());
        if (!isSequential) {
            throw new FlowableBizException(WorkflowResultCode.CAN_NOT_RETURN);
        }
        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需退回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要撤回的任务
        List<UserTask> currentUserTaskList =
                FlowableUtils.iteratorFindChildUserTasks(target, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        identityService.setAuthenticatedUserId(user.getUserId());
        // 设置流程状态为已驳回
        updateStatus(flowableAuditRequest.getProcessInstanceId(), ProcessStateEnum.REJECT, task);
        // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetKey 跳转到的节点(1)
        runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId())
                .moveActivityIdsToSingleActivityId(currentIds, flowableAuditRequest.getTargetKey()).changeState();
        addCommentTask(FlowCommentEnum.REBACK, flowableAuditRequest.getComment(), task);
    }

    @Override
    @MultiTransactional(value = {"mybatisTransactionManager", "mongoTransactionManager"})
    public void delegateTask(FlowableAuditRequest flowableAuditRequest) {
        Task exist = taskService.createTaskQuery().includeProcessVariables()
                .processInstanceId(flowableAuditRequest.getProcessInstanceId())
                .taskDefinitionId(flowableAuditRequest.getTaskKey())
                .taskAssignee(flowableAuditRequest.getUserId().toString()).singleResult();
        if (exist != null) {
            throw new FlowableBizException(WorkflowResultCode.USER_EXIST_TASK);
        }
        // 当前任务 task
        UserDomain user = UserUtils.getUser();
        // 查询任务id 或 流程实例id
        setTaskAndProcessInstanceId(flowableAuditRequest);
        String taskId = flowableAuditRequest.getTaskId();
        Task task = taskService.createTaskQuery().includeProcessVariables().taskId(taskId).singleResult();
        StringBuilder commentBuilder = new StringBuilder(user.getNickName()).append("委派给");
        String nickName = userService.getIdToNameMap(Collections.singletonList(flowableAuditRequest.getUserId()))
                .get(flowableAuditRequest.getUserId());
        if (StringUtils.isNotBlank(nickName)) {
            commentBuilder.append(nickName);
        } else {
            commentBuilder.append(flowableAuditRequest.getUserId());
        }
        if (StringUtils.isNotBlank(flowableAuditRequest.getComment())) {
            commentBuilder.append(": ").append(flowableAuditRequest.getComment());
        }

        // 设置流程状态为已委派
        updateStatus(flowableAuditRequest.getProcessInstanceId(), ProcessStateEnum.DELEGATE, task);
        Object object = task.getProcessVariables().get(task.getTaskDefinitionKey() + "_isMultiInstance");
        if (object != null && Boolean.parseBoolean(object.toString())) {
            // 转办任务
            taskService.setAssignee(taskId, flowableAuditRequest.getUserId().toString());
        } else {
            taskService.addCandidateUser(task.getId(), flowableAuditRequest.getUserId().toString());
            taskService.deleteCandidateUser(task.getId(), user.getUserId());
        }
        addCommentTask(FlowCommentEnum.DELEGATE, commentBuilder.toString(), task);
    }

    @Override
    @MultiTransactional(value = {"mybatisTransactionManager", "mongoTransactionManager"})
    public void changeApproval(FlowableAuditRequest flowableAuditRequest) {
        // 查询任务id 或 流程实例id

        List<Task> taskList = taskService.createTaskQuery().taskDefinitionKey(flowableAuditRequest.getTaskKey())
                .includeProcessVariables().processInstanceId(flowableAuditRequest.getProcessInstanceId()).list();
        Task exist = taskList.get(0);
        Object object = exist.getProcessVariables().get(exist.getTaskDefinitionKey() + "_isMultiInstance");
        List<String> assigneeUserList = flowableActivityConfigService.getAssigneeUserList(exist.getProcessVariables(),
                flowableAuditRequest.getFlowableAssigneeConfigList());
        if (object != null && Boolean.parseBoolean(object.toString())) {
            changeApprovalMulti(assigneeUserList, exist, taskList);
        } else {
            changeApproval(assigneeUserList, exist);
        }
        addCommentInstance(flowableAuditRequest, exist, FlowCommentEnum.TRANSFER);
    }

    private void changeApproval(List<String> assigneeUserList, Task exist) {
        List<String> candidateGroups = taskService.getIdentityLinksForTask(exist.getId()).stream()
                .filter(identityLink -> identityLink.getType().equals("candidate")).map(IdentityLinkInfo::getUserId)
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(candidateGroups)) {
            for (String candidateGroup : candidateGroups) {
                taskService.deleteCandidateUser(exist.getId(), candidateGroup);
            }
        }

        for (String assigneeUser : assigneeUserList) {
            taskService.addCandidateUser(exist.getId(), assigneeUser);
        }
    }

    private void changeApprovalMulti(List<String> assigneeUserList, Task exist, List<Task> list) {
        List<String> assigneeList = list.stream().map(TaskInfo::getAssignee).collect(Collectors.toList());
        for (String assigneeName : assigneeUserList) {
            if (!assigneeList.contains(assigneeName)) {
                Map<String, Object> multiMap = new HashMap<>();
                multiMap.put("assignee", assigneeName);
                runtimeService.addMultiInstanceExecution(exist.getTaskDefinitionKey(), exist.getProcessInstanceId(),
                        multiMap);
            }
        }
        Map<String, String> idToExecutionIdMap =
                list.stream().collect(Collectors.toMap(TaskInfo::getAssignee, TaskInfo::getExecutionId));
        for (String assigneeName : assigneeList) {
            String executionId = idToExecutionIdMap.get(assigneeName);
            if (!assigneeUserList.contains(assigneeName)) {
                runtimeService.deleteMultiInstanceExecution(executionId, false);
            }
        }
    }

    @Override
    public void transferTask(FlowableAuditRequest flowableAuditRequest) {
        Task exist = taskService.createTaskQuery().includeProcessVariables()
                .processInstanceId(flowableAuditRequest.getProcessInstanceId())
                .taskDefinitionId(flowableAuditRequest.getTaskKey())
                .taskAssignee(flowableAuditRequest.getUserId().toString()).singleResult();
        if (exist != null) {
            throw new FlowableBizException(WorkflowResultCode.USER_EXIST_TASK);
        }

        // 当前任务 task
        UserDomain user = UserUtils.getUser();
        // 查询任务id 或 流程实例id
        setTaskAndProcessInstanceId(flowableAuditRequest);
        String taskId = flowableAuditRequest.getTaskId();
        Task task = taskService.createTaskQuery().includeProcessVariables().taskId(taskId).singleResult();
        StringBuilder commentBuilder = new StringBuilder(user.getNickName()).append("->");
        String nickName = userService.getIdToNameMap(Collections.singletonList(flowableAuditRequest.getUserId()))
                .get(flowableAuditRequest.getUserId());
        if (StringUtils.isNotBlank(nickName)) {
            commentBuilder.append(nickName);
        } else {
            commentBuilder.append(flowableAuditRequest.getUserId());
        }
        if (StringUtils.isNotBlank(flowableAuditRequest.getComment())) {
            commentBuilder.append(": ").append(flowableAuditRequest.getComment());
        }
        // 设置流程状态为已委派
        updateStatus(flowableAuditRequest.getProcessInstanceId(), ProcessStateEnum.TRANSFER, task);
        // 设置拥有者为当前登录人
        taskService.setOwner(taskId, user.getUserId());
        // 转办任务
        taskService.setAssignee(taskId, flowableAuditRequest.getUserId().toString());
        // 添加审批意见
        addCommentTask(FlowCommentEnum.TRANSFER, commentBuilder.toString(), task);
    }

    @Override
    public void taskClaim(FlowableAuditRequest flowableAuditRequest) {
        setTaskAndProcessInstanceId(flowableAuditRequest);
        Task task = taskService.createTaskQuery().includeProcessVariables().taskId(flowableAuditRequest.getTaskId())
                .singleResult();
        identityService.setAuthenticatedUserId(UserUtils.getUser().getUserId());
        taskService.claim(task.getId(), UserUtils.getUser().getUserId());
        // 添加审批意见
        taskService.addComment(flowableAuditRequest.getTaskId(), flowableAuditRequest.getProcessInstanceId(),
                FlowCommentEnum.CLAIM.name(), flowableAuditRequest.getComment());
    }

    /**
     * 查询任务id 或 流程实例id
     *
     * @param flowableAuditRequest
     */
    private void setTaskAndProcessInstanceId(FlowableAuditRequest flowableAuditRequest) {
        if (StringUtils.isEmpty(flowableAuditRequest.getProcessInstanceId()) &&
                StringUtils.isEmpty(flowableAuditRequest.getTaskId())) {
            throw new FlowableBizException(WorkflowResultCode.WORKFLOW_INSTANCE_TASK_ALL_EMPTY);
        }
        if (StringUtils.isEmpty(flowableAuditRequest.getTaskId())) {
            flowableAuditRequest.setTaskId(
                    taskService.createTaskQuery().processInstanceId(flowableAuditRequest.getProcessInstanceId())
                            .singleResult().getId());
        }
        if (StringUtils.isEmpty(flowableAuditRequest.getProcessInstanceId())) {
            flowableAuditRequest.setProcessInstanceId(
                    taskService.createTaskQuery().taskId(flowableAuditRequest.getTaskId()).singleResult()
                            .getProcessInstanceId());
        }
    }

    /**
     * 添加评论加抄送 回退需自己特殊处理
     *
     * @param flowCommentEnum 评论类型
     * @param comment
     * @param task
     */
    private void addCommentTask(FlowCommentEnum flowCommentEnum, String comment, Task task) {
        FlowableOperateLogRequest flowableOperateLogRequest = new FlowableOperateLogRequest();
        flowableOperateLogRequest.setComment(comment);
        flowableOperateLogRequest.setTaskId(task.getId());
        flowableOperateLogRequest.setTaskName(task.getName());
        flowableOperateLogRequest.setTaskKey(task.getTaskDefinitionKey());
        flowableOperateLogRequest.setCreateTime(new Date());
        flowableOperateLogRequest.setProcessInstanceId(task.getProcessInstanceId());
        flowableOperateLogRequest.setOperate(flowCommentEnum.getType());
        flowableOperateLogRequest.setTaskCreateTime(task.getCreateTime());
        flowableOperateLogService.saveLog(flowableOperateLogRequest);
    }

    @Override
    public QueryPageVO<OwnerTaskVO> getOwnerList(FlowableOwnerListRequest flowableOwnerListRequest) {
        UserDomain user = UserUtils.getUser();
        HistoricProcessInstanceQuery historicProcessInstanceQuery =
                historyService.createHistoricProcessInstanceQuery().includeProcessVariables()
                        .startedBy(user.getUserId()).orderByProcessInstanceStartTime().desc();
        historicProcessInstanceQuery.processInstanceTenantId(user.getCompanyId().toString());
        // 构建搜索条件
        FlowablePageQueryDomain process = AbstractFlowableConverter.INSTANCE.toQueryDomain(flowableOwnerListRequest);
        ProcessUtils.buildProcessSearch(historicProcessInstanceQuery, process);
        List<HistoricProcessInstance> historicProcessInstances =
                historicProcessInstanceQuery.listPage(flowableOwnerListRequest.getOffSet(),
                        flowableOwnerListRequest.getPageSize());
        long total = historicProcessInstanceQuery.count();
        if (total == 0) {
            return new QueryPageVO<>(0, new ArrayList<>());
        }
        Set<String> processInstanceIdList =
                historicProcessInstances.stream().map(HistoricProcessInstance::getId).collect(Collectors.toSet());
        // 当前所处流程
        List<Task> tasks =
                taskService.createTaskQuery().processInstanceIdIn(processInstanceIdList).includeIdentityLinks()
                        .includeProcessVariables().list();
        List<OwnerTaskVO> ownerTaskVOS = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            OwnerTaskVO taskVo = new OwnerTaskVO();
            // 获取流程状态
            HistoricVariableInstance processStatusVariable =
                    historyService.createHistoricVariableInstanceQuery().processInstanceId(hisIns.getId())
                            .variableName(FlowableConstant.PROCESS_STATUS_SING_KEY).singleResult();
            String processStatus = null;
            if (ObjectUtil.isNotNull(processStatusVariable)) {
                processStatus = Convert.toStr(processStatusVariable.getValue());
            }
            taskVo.setProcessStatus(processStatus);
            taskVo.setCreateTime(hisIns.getStartTime());
            taskVo.setFinishTime(hisIns.getEndTime());
            taskVo.setProcessVariables(hisIns.getProcessVariables());
            taskVo.setProcessInstanceId(hisIns.getId());
            taskVo.setDeployId(hisIns.getDeploymentId());
            taskVo.setProcDefId(hisIns.getProcessDefinitionId());
            taskVo.setProcDefName(hisIns.getProcessDefinitionName());
            taskVo.setProcDefVersion(hisIns.getProcessDefinitionVersion());
            List<Task> taskList = tasks.stream().filter(c -> c.getProcessInstanceId().equals(hisIns.getId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(taskList)) {
                taskVo.setTaskName(taskList.stream().map(Task::getName).filter(StringUtils::isNotEmpty).distinct()
                        .collect(Collectors.joining("，")));
            }
            ownerTaskVOS.add(taskVo);
        }
        return new QueryPageVO<>((int) total, ownerTaskVOS);
    }

    @Override
    public List<PendingTaskVO> getTaskByProcessInstanceIdList(List<String> processInstanceIdList) {
        // 当前所处流程
        List<Task> tasks =
                taskService.createTaskQuery().includeProcessVariables().processInstanceIdIn(processInstanceIdList)
                        .includeIdentityLinks().includeProcessVariables().list();
        Set<String> processDefinitionIdList =
                tasks.stream().map(TaskInfo::getProcessDefinitionId).collect(Collectors.toSet());
        List<ProcessDefinition> processDefinitionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(processDefinitionIdList)) {
            // 流程定义信息
            processDefinitionList =
                    repositoryService.createProcessDefinitionQuery().processDefinitionIds(processDefinitionIdList)
                            .list();
        }
        List<ProcessDefinition> finalProcessDefinitionList = processDefinitionList;
        return tasks.stream()
                .map(c -> getPendingTaskVO(c, new ArrayList<>(), finalProcessDefinitionList, new HashMap<>()))
                .collect(Collectors.toList());
    }

    @Override
    public QueryPageVO<PendingTaskVO> getTodoList(FlowableToDoListRequest flowableToDoListRequest) {
        UserDomain user = UserUtils.getUser();
        // 构建查询条件
        TaskQuery condition = taskService.createTaskQuery().active().includeProcessVariables()
                .taskCandidateOrAssigned(user.getUserId()).taskCandidateUser(user.getUserId())
                .taskTenantId(UserUtils.getUser().getCompanyId().toString());
        if (CollectionUtils.isNotEmpty(flowableToDoListRequest.getProcessInstanceIds())) {
            condition = condition.processInstanceIdIn(flowableToDoListRequest.getProcessInstanceIds());
        }
        FlowablePageQueryDomain process = AbstractFlowableConverter.INSTANCE.toQueryDomain(flowableToDoListRequest);
        ProcessUtils.buildProcessSearch(condition, process);
        long total = condition.count();
        if (total == 0) {
            return new QueryPageVO<>(0, new ArrayList<>());
        }
        List<Task> taskList = condition.active().orderByTaskCreateTime().desc()
                .listPage(flowableToDoListRequest.getOffSet(), flowableToDoListRequest.getPageSize());
        Set<String> processInstanceIdList =
                taskList.stream().map(TaskInfo::getProcessInstanceId).collect(Collectors.toSet());
        List<HistoricProcessInstance> historicProcessInstanceList =
                historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdList).list();

        Set<String> processDefinitionIdList =
                taskList.stream().map(TaskInfo::getProcessDefinitionId).collect(Collectors.toSet());
        // 流程定义信息
        List<ProcessDefinition> processDefinitionList =
                repositoryService.createProcessDefinitionQuery().processDefinitionIds(processDefinitionIdList).list();

        List<PendingTaskVO> pendingTaskVOList = new ArrayList<>();
        List<Long> userIdList = historicProcessInstanceList.stream()
                .map(c -> c.getStartUserId() == null ? 0 : Long.parseLong(c.getStartUserId()))
                .collect(Collectors.toList());
        Map<Long, String> idToNameMap = userService.getIdToNameMap(userIdList);
        List<String> deploymentIdList =
                processDefinitionList.stream().map(ProcessDefinition::getDeploymentId).collect(Collectors.toList());
        Map<String, String> deploymentIdToModelMap = reModelService.getByDeploymentId(deploymentIdList);
        List<FlowableActivityConfigDomain> flowableActivityConfigList =
                flowableActivityConfigService.getByModelIdList(new ArrayList<>(deploymentIdToModelMap.values()),
                        "approval");
        Map<String, FlowableActivityConfigDomain> keyToConfigMap = flowableActivityConfigList.stream()
                .collect(Collectors.toMap(c -> c.getModelId() + "_" + c.getActivityId(), c -> c));
        for (Task task : taskList) {
            PendingTaskVO pendingTaskVO =
                    getPendingTaskVO(task, historicProcessInstanceList, processDefinitionList, idToNameMap);
            String modelId = deploymentIdToModelMap.get(pendingTaskVO.getDeployId());
            if (modelId != null) {
                String key = modelId + "_" + task.getTaskDefinitionKey();
                FlowableActivityConfigDomain flowableActivityConfigDomain = keyToConfigMap.get(key);
                if (flowableActivityConfigDomain != null) {
                    pendingTaskVO.setFieldConfigList(flowableActivityConfigDomain.getFieldConfigList());
                    pendingTaskVO.setOtherConfig(JSONObject.parseObject(flowableActivityConfigDomain.getOtherConfig(),
                            FlowableFormOtherConfig.class));
                }
            }
            pendingTaskVOList.add(pendingTaskVO);
        }
        return new QueryPageVO<>((int) total, pendingTaskVOList);
    }

    @Override
    public QueryPageVO<PendingTaskVO> getTodoListOnlyTask(FlowableToDoListRequest flowableToDoListRequest) {
        UserDomain user = UserUtils.getUser();
        // 构建查询条件
        TaskQuery condition = taskService.createTaskQuery().active().includeProcessVariables()
                .taskCandidateOrAssigned(user.getUserId()).taskCandidateUser(user.getUserId())
                .taskTenantId(UserUtils.getUser().getCompanyId().toString());
        if (CollectionUtils.isNotEmpty(flowableToDoListRequest.getProcessInstanceIds())) {
            condition = condition.processInstanceIdIn(flowableToDoListRequest.getProcessInstanceIds());
        }
        FlowablePageQueryDomain process = AbstractFlowableConverter.INSTANCE.toQueryDomain(flowableToDoListRequest);
        ProcessUtils.buildProcessSearch(condition, process);
        long total = condition.count();
        if (total == 0) {
            return new QueryPageVO<>(0, new ArrayList<>());
        }
        List<Task> taskList = condition.active().orderByTaskCreateTime().desc()
                .listPage(flowableToDoListRequest.getOffSet(), flowableToDoListRequest.getPageSize());
        Set<String> processInstanceIdList =
                taskList.stream().map(TaskInfo::getProcessInstanceId).collect(Collectors.toSet());
        List<HistoricProcessInstance> historicProcessInstanceList =
                historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdList).list();

        List<PendingTaskVO> pendingTaskVOList = new ArrayList<>();
        for (Task task : taskList) {
            PendingTaskVO pendingTaskVO =
                    getPendingTaskVO(task, historicProcessInstanceList, new ArrayList<>(), new HashMap<>());
            pendingTaskVOList.add(pendingTaskVO);
        }
        return new QueryPageVO<>((int) total, pendingTaskVOList);
    }

    @Override
    public List<String> getProcessInstanceIdListByTaskId(List<String> taskKeys, String processDefinitionKey) {
        return taskService.createTaskQuery().taskDefinitionKeys(taskKeys).processDefinitionKey(processDefinitionKey)
                .list().stream().map(TaskInfo::getProcessInstanceId).collect(Collectors.toList());

    }

    @Override
    public List<String> getProcessInstanceIdListByAssigneeId(List<String> assigneeIds, String processDefinitionKey) {
        return taskService.createTaskQuery().taskAssigneeIds(assigneeIds).processDefinitionKey(processDefinitionKey)
                .list().stream().map(TaskInfo::getProcessInstanceId).collect(Collectors.toList());
    }

    private static PendingTaskVO getPendingTaskVO(Task task, List<HistoricProcessInstance> historicProcessInstanceList,
                                                  List<ProcessDefinition> processDefinitionList,
                                                  Map<Long, String> idToNameMap) {
        HistoricProcessInstance historicProcessInstance =
                historicProcessInstanceList.stream().filter(c -> c.getId().equals(task.getProcessInstanceId()))
                        .findFirst().orElse(null);
        ProcessDefinition processDefinition =
                processDefinitionList.stream().filter(c -> c.getId().equals(task.getProcessDefinitionId())).findFirst()
                        .orElse(null);
        PendingTaskVO pendingTaskVO = new PendingTaskVO();
        pendingTaskVO.setTaskName(task.getName());
        pendingTaskVO.setTaskDefKey(task.getTaskDefinitionKey());
        pendingTaskVO.setCreateTime(task.getCreateTime());
        pendingTaskVO.setProcDefId(task.getProcessDefinitionId());
        pendingTaskVO.setProcessInstanceId(task.getProcessInstanceId());
        pendingTaskVO.setTaskId(task.getId());
        pendingTaskVO.setProcessVariables(task.getProcessVariables());
        if (historicProcessInstance != null) {
            String startUserId = historicProcessInstance.getStartUserId();
            if (startUserId == null) {
                startUserId = "0";
            }
            pendingTaskVO.setStartUserId(startUserId);
            pendingTaskVO.setStartUserName(idToNameMap.get(Long.valueOf(startUserId)));
        }
        if (processDefinition != null) {
            pendingTaskVO.setProcDefKey(processDefinition.getKey());
            pendingTaskVO.setDeployId(processDefinition.getDeploymentId());
            pendingTaskVO.setProcDefName(processDefinition.getName());
            pendingTaskVO.setProcDefVersion(processDefinition.getVersion());
        }
        return pendingTaskVO;
    }

    @Override
    public QueryPageVO<DoneTaskVO> getDoneList(FlowableDoneListRequest flowableDoneListRequest) {
        UserDomain user = UserUtils.getUser();
        HistoricTaskInstanceQuery taskInstanceQuery =
                historyService.createHistoricTaskInstanceQuery().finished(); // 只查询已完成的流程实例
        taskInstanceQuery.taskTenantId(UserUtils.getUser().getCompanyId().toString());
        taskInstanceQuery.taskAssignee(user.getUserId()).orderByHistoricTaskInstanceEndTime().desc();

        FlowablePageQueryDomain process = AbstractFlowableConverter.INSTANCE.toQueryDomain(flowableDoneListRequest);
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskInstanceQuery, process);
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.includeProcessVariables()
                .listPage(flowableDoneListRequest.getOffSet(), flowableDoneListRequest.getPageSize());
        long total = taskInstanceQuery.count();
        Set<String> processInstanceIdList =
                historicTaskInstanceList.stream().map(TaskInfo::getProcessInstanceId).collect(Collectors.toSet());

        Set<String> processDefinitionIdList =
                historicTaskInstanceList.stream().map(TaskInfo::getProcessDefinitionId).collect(Collectors.toSet());

        // 流程定义信息
        List<ProcessDefinition> processDefinitionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(processDefinitionIdList)) {
            processDefinitionList =
                    repositoryService.createProcessDefinitionQuery().processDefinitionIds(processDefinitionIdList)
                            .list();
        }

        // 流程发起人信息
        List<HistoricProcessInstance> historicProcessInstanceList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
            historicProcessInstanceList =
                    historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdList)
                            .list();
        }

        List<Long> userIdList = historicProcessInstanceList.stream().map(c -> Long.valueOf(c.getStartUserId()))
                .collect(Collectors.toList());
        Map<Long, String> idToNameMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(userIdList)) {
            idToNameMap = userService.getIdToNameMap(userIdList);
        }

        List<DoneTaskVO> doneTaskVOList = new ArrayList<>();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            DoneTaskVO doneTaskVO = new DoneTaskVO();
            // 当前流程信息
            doneTaskVO.setTaskId(histTask.getId());
            // 审批人员信息
            doneTaskVO.setCreateTime(histTask.getCreateTime());
            doneTaskVO.setFinishTime(histTask.getEndTime());
            doneTaskVO.setDuration(
                    DateUtil.formatBetween(histTask.getDurationInMillis(), BetweenFormatter.Level.SECOND));
            doneTaskVO.setProcDefId(histTask.getProcessDefinitionId());
            doneTaskVO.setTaskDefKey(histTask.getTaskDefinitionKey());
            doneTaskVO.setTaskName(histTask.getName());
            ProcessDefinition pd =
                    processDefinitionList.stream().filter(c -> c.getId().equals(histTask.getProcessDefinitionId()))
                            .findFirst().orElse(null);
            if (pd != null) {
                doneTaskVO.setDeployId(pd.getDeploymentId());
                doneTaskVO.setProcDefName(pd.getName());
                doneTaskVO.setProcDefVersion(pd.getVersion());
                doneTaskVO.setProcessInstanceId(histTask.getProcessInstanceId());
            }
            HistoricProcessInstance historicProcessInstance =
                    historicProcessInstanceList.stream().filter(c -> c.getId().equals(histTask.getProcessInstanceId()))
                            .findFirst().orElse(null);
            if (historicProcessInstance != null) {
                doneTaskVO.setStartUserId(historicProcessInstance.getStartUserId());
                doneTaskVO.setStartUserName(idToNameMap.get(Long.valueOf(historicProcessInstance.getStartUserId())));
            }
            // 流程变量
            doneTaskVO.setProcessVariables(histTask.getProcessVariables());
            doneTaskVOList.add(doneTaskVO);
        }
        return new QueryPageVO<>((int) total, doneTaskVOList);
    }

    @Override
    public FlowableDetailVO getProcessInstanceDetail(String processInstanceId) {
        FlowableDetailVO detailVo = new FlowableDetailVO();
        // 获取流程实例
        HistoricProcessInstance historicProcIns =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
                        .includeProcessVariables().singleResult();
        if (StringUtils.isNotEmpty(historicProcIns.getTenantId())) {
            corpCoopComponent.exchangeCompanyId(Long.valueOf(historicProcIns.getTenantId()));
        }
        ModelVO modelVO = modelManageService.infoByProcessDefinitionId(historicProcIns.getProcessDefinitionId());
        InputStream inputStream = repositoryService.getProcessModel(historicProcIns.getProcessDefinitionId());
        String bpmnXmlStr = StrUtil.utf8Str(IoUtil.readBytes(inputStream, false));
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXmlStr);
        detailVo.setHistoryProcNodeList(historyProcNodeList(historicProcIns, modelVO.getModelId()));
        detailVo.setFlowViewer(getFlowViewer(bpmnModel, processInstanceId));
        return detailVo;
    }

    @Override
    // @Transactional(rollbackFor = Exception.class)
    public void terminateByParent(String parentProcessInstanceId) {
        TaskQuery taskQuery = taskService.createTaskQuery().active().includeProcessVariables().includeProcessVariables()
                .processVariableValueEquals(FlowableConstant.PARENT_PROCESS_INSTANCE_ID, parentProcessInstanceId);
        List<Task> taskList = taskQuery.active().orderByTaskCreateTime().desc().list();
        for (Task task : taskList) {
            FlowableAuditRequest flowableAuditRequest = new FlowableAuditRequest();
            flowableAuditRequest.setTaskId(task.getId());
            flowableAuditRequest.setProcessInstanceId(task.getProcessInstanceId());
            flowableAuditRequest.setComment("任务终止");
            taskReject(flowableAuditRequest);
        }
    }

    @Override
    public void updateVariable(String processInstanceId, Map<String, Object> variable) {
        runtimeService.setVariables(processInstanceId, variable);
    }

    @Override
    public void copy(HistoricTaskInstance task, Map<String, Object> processVariables, JSONObject jsonObject) {
        ProcessDefinition processDefinition =
                repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId())
                        .singleResult();
        Model model =
                repositoryService.createModelQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
        FlowableCopyCreateRequest flowableCopyCreateRequest = new FlowableCopyCreateRequest();
        if (StringUtils.isNotEmpty(model.getTenantId())) {
            flowableCopyCreateRequest.setCompanyId(Long.valueOf(model.getTenantId()));
        }
        flowableCopyCreateRequest.setProcessInstanceId(task.getProcessInstanceId());
        flowableCopyCreateRequest.setTaskId(task.getId());
        flowableCopyCreateRequest.setTaskName(task.getName());
        flowableCopyCreateRequest.setActivityId(task.getTaskDefinitionKey());
        flowableCopyCreateRequest.setModelId(model.getId());
        flowableCopyCreateRequest.setProcessDefinitionId(task.getProcessDefinitionId());
        flowableCopyCreateRequest.setProcessDefinitionName(processDefinition.getName());
        flowableCopyCreateRequest.setBusinessType(processDefinition.getKey());
        flowableCopyCreateRequest.setApplicationId(task.getCategory());
        Object initiatorObject = processVariables.get(FlowableConstant.PROCESS_INITIATOR);
        Object formId = processVariables.get(FlowableConstant.FORM_ID);
        if (initiatorObject != null) {
            flowableCopyCreateRequest.setInitiator(initiatorObject.toString());
        }
        if (formId != null) {
            flowableCopyCreateRequest.setFormId(formId.toString());
        }
        Object applicationId = processVariables.get(FlowableConstant.VARIABLE_APPLICATION_ID);
        if (applicationId != null) {
            flowableCopyCreateRequest.setApplicationId(applicationId.toString());
        }

        Object dataUuid = processVariables.get(FlowableConstant.VARIABLE_DATA_UUID);
        if (dataUuid != null) {
            flowableCopyCreateRequest.setDataUuid(dataUuid.toString());
        }
        flowableCopyCreateRequest.setProcessVariables(processVariables);
        flowableCopyCreateRequest.setInstValue(jsonObject);
        flowableCopyService.create(flowableCopyCreateRequest);
    }

    @Override
    public void deleteDataByProcessDefinitionKey(String processDefinitionKey) {
        List<ProcessInstance> processInstanceList =
                runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
        for (ProcessInstance processInstance : processInstanceList) {
            runtimeService.deleteProcessInstance(processInstance.getId(), "删除");
        }
        List<HistoricProcessInstance> historicProcessInstanceList =
                historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
        for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
            historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
        }
    }


    /**
     * 获取历史任务信息列表
     */
    private List<FlowableOperateLogVO> historyProcNodeList(HistoricProcessInstance historicProcIns, String modelId) {
        String procInsId = historicProcIns.getId();
        List<HistoricActivityInstance> historicActivityInstanceList =
                historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId).activityTypes(
                                CollUtil.newHashSet(BpmnXMLConstants.ELEMENT_EVENT_START, BpmnXMLConstants.ELEMENT_EVENT_END,
                                        BpmnXMLConstants.ELEMENT_TASK_USER)).orderByHistoricActivityInstanceStartTime().desc()
                        .orderByHistoricActivityInstanceEndTime().desc().list();
        List<ProcessNodeVO> elementVoList = new ArrayList<>();
        List<Long> assigneeList = historicActivityInstanceList.stream().filter(c -> c.getAssignee() != null)
                .map(c -> Long.valueOf(c.getAssignee())).collect(Collectors.toList());

        ProcessNodeVO dealProcessNodeVO = new ProcessNodeVO();

        long processEndTime = 0L;
        ProcessNodeVO endProcessNode = new ProcessNodeVO();
        List<FlowableActivityConfigDomain> flowableActivityConfigDomainList =
                flowableActivityConfigService.detail(modelId, null);
        Map<String, FlowableActivityConfigDomain> activityIdToTypeMap = flowableActivityConfigDomainList.stream()
                .collect(Collectors.toMap(FlowableActivityConfigDomain::getActivityId, c -> c));

        List<HiIdentitylinkEntity> hiIdentitylinkEntityList = hiIdentitylinkService.getByTaskIdList(
                historicActivityInstanceList.stream().map(HistoricActivityInstance::getTaskId)
                        .collect(Collectors.toList()));
        List<String> userIdList =
                hiIdentitylinkEntityList.stream().map(HiIdentitylinkEntity::getUserId).collect(Collectors.toList());
        Map<String, List<HiIdentitylinkEntity>> taskIdMap =
                hiIdentitylinkEntityList.stream().collect(Collectors.groupingBy(HiIdentitylinkEntity::getTaskId));
        assigneeList.addAll(userIdList.stream().map(Long::valueOf).collect(Collectors.toList()));
        Map<Long, String> userNameMap = userService.getIdToNameMap(assigneeList);
        for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
            // 获取意见评论内容
            ProcessNodeVO elementVo = new ProcessNodeVO();
            if (activityInstance.getActivityId().equals(dealProcessNodeVO.getActivityId())) {
                if (!dealProcessNodeVO.getEnd()) {
                    if (activityInstance.getEndTime() != null) {
                        continue;
                    }
                }
                addAssigneeAndCandidate(activityInstance, userNameMap, dealProcessNodeVO, taskIdMap,
                        historicProcIns.getProcessVariables());
                continue;
            }
            dealProcessNodeVO = elementVo;
            elementVo.setProcDefId(activityInstance.getProcessDefinitionId());
            elementVo.setActivityId(activityInstance.getActivityId());
            elementVo.setActivityName(activityInstance.getActivityName());
            FlowableActivityConfigDomain flowableActivityConfigDomain =
                    activityIdToTypeMap.get(activityInstance.getActivityId());
            if (flowableActivityConfigDomain != null) {
                elementVo.setActivityType(flowableActivityConfigDomain.getActivityType());
                elementVo.setAuditType(flowableActivityConfigDomain.getAuditType());
            } else {
                elementVo.setActivityType(activityInstance.getActivityType());
            }
            elementVo.setCreateTime(activityInstance.getStartTime());
            elementVo.setEndTime(activityInstance.getEndTime());
            elementVo.setEnd(activityInstance.getEndTime() != null);
            if (ObjectUtil.isNotNull(activityInstance.getDurationInMillis())) {
                elementVo.setDuration(
                        DateUtil.formatBetween(activityInstance.getDurationInMillis(), BetweenFormatter.Level.SECOND));
            }

            if (BpmnXMLConstants.ELEMENT_EVENT_START.equals(activityInstance.getActivityType())) {
                if (ObjectUtil.isNotNull(historicProcIns)) {
                    Long userId = Long.parseLong(historicProcIns.getStartUserId());
                    String nickName = userNameMap.get(userId);

                    if (nickName != null) {
                        elementVo.setAssigneeId(userId);
                        elementVo.setAssigneeName(nickName);
                    }
                }
                endProcessNode.setDuration(
                        DateUtil.formatBetween((processEndTime - activityInstance.getStartTime().getTime()),
                                BetweenFormatter.Level.SECOND));

            } else if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(activityInstance.getActivityType())) {
                addAssigneeAndCandidate(activityInstance, userNameMap, elementVo, taskIdMap,
                        historicProcIns.getProcessVariables());
            } else if (BpmnXMLConstants.ELEMENT_EVENT_END.equals(activityInstance.getActivityType())) {
                processEndTime = elementVo.getEndTime().getTime();
                endProcessNode = elementVo;
            }
            elementVoList.add(elementVo);
        }
        List<FlowableOperateLogVO> flowableOperateLogVOS = flowableOperateLogService.queryByInstanceId(procInsId);
        int i = 0;
        for (ProcessNodeVO processNodeVO : elementVoList) {
            FlowableOperateLogVO flowableOperateLogVO = new FlowableOperateLogVO();
            flowableOperateLogVO.setActivityType(processNodeVO.getActivityType());
            flowableOperateLogVO.setAuditType(processNodeVO.getAuditType());
            if ("end".equals(processNodeVO.getActivityId())) {
                flowableOperateLogVO.setTaskKey("end");
                flowableOperateLogVO.setTaskName("流程结束");
                flowableOperateLogVO.setDuration(processNodeVO.getDuration());
                flowableOperateLogVOS.add(0, flowableOperateLogVO);
                break;
            } else {
                if (processNodeVO.getEnd()) {
                    continue;
                }
                if ("approval".equals(processNodeVO.getActivityType())) {
                    flowableOperateLogVO.setTaskKey(processNodeVO.getActivityId());
                    flowableOperateLogVO.setTaskName(processNodeVO.getActivityName());
                    flowableOperateLogVO.setCandidateList(processNodeVO.getCandidateList());
                    flowableOperateLogVO.setCreateTime(processNodeVO.getCreateTime());
                    flowableOperateLogVO.setActivityName(flowableOperateLogVO.getTaskName());
                } else if ("subFlowTask".equals(processNodeVO.getActivityType())) {
                    flowableOperateLogVO.setTaskKey(processNodeVO.getActivityId());
                    flowableOperateLogVO.setTaskName(processNodeVO.getActivityName());
                    flowableOperateLogVO.setCandidateList(processNodeVO.getCandidateList());
                    flowableOperateLogVO.setAssigneeList(processNodeVO.getAssigneeList());
                    flowableOperateLogVO.setCreateTime(processNodeVO.getCreateTime());
                    flowableOperateLogVO.setActivityName(flowableOperateLogVO.getTaskName());
                }
                flowableOperateLogVOS.add(i, flowableOperateLogVO);
                i++;
            }

        }
        return flowableOperateLogVOS;
    }

    private void addAssigneeAndCandidate(HistoricActivityInstance activityInstance, Map<Long, String> userNameMap,
                                         ProcessNodeVO dealProcessNodeVO,
                                         Map<String, List<HiIdentitylinkEntity>> taskIdMap,
                                         Map<String, Object> processVariables) {
        if ("approval".equals(dealProcessNodeVO.getActivityType()) &&
                ApprovalMultiEnum.SINGLE.getMulti().equals(dealProcessNodeVO.getAuditType())) {
            Object multiInstance = processVariables.get(activityInstance.getActivityId() + "_isMultiInstance");
            if (multiInstance != null && Boolean.parseBoolean(multiInstance.toString())) {
                addCandidateDomain(activityInstance, userNameMap, dealProcessNodeVO);
            } else {
                List<HiIdentitylinkEntity> hiIdentitylinkEntities = taskIdMap.get(activityInstance.getTaskId());
                addCandidateDomainList(activityInstance, hiIdentitylinkEntities, dealProcessNodeVO, userNameMap);
            }
        } else if ("subFlowTask".equals(dealProcessNodeVO.getActivityType())) {
            addCandidateDomain(activityInstance, userNameMap, dealProcessNodeVO);
        } else if ("approval".equals(dealProcessNodeVO.getActivityType()) &&
                ApprovalMultiEnum.JOINT.getMulti().equals(dealProcessNodeVO.getAuditType()) &&
                activityInstance.getEndTime() == null) {
            addCandidateDomain(activityInstance, userNameMap, dealProcessNodeVO);
        }
        if ((StringUtils.isNotEmpty(activityInstance.getAssignee()) && activityInstance.getEndTime() != null) ||
                "subFlowTask".equals(activityInstance.getActivityType())) {
            addAssignee(activityInstance.getAssignee(), activityInstance.getTaskId(), userNameMap, dealProcessNodeVO);
        }
    }


    private void addCandidateDomainList(HistoricActivityInstance activityInstance,
                                        List<HiIdentitylinkEntity> linksForTask, ProcessNodeVO dealProcessNodeVO,
                                        Map<Long, String> userNameMap) {
        List<Long> userIdList = new ArrayList<>();
        for (HiIdentitylinkEntity historicIdentityLink : linksForTask) {
            if (StringUtils.isEmpty(historicIdentityLink.getUserId())) {
                continue;
            }
            userIdList.add(Long.valueOf(historicIdentityLink.getUserId()));
        }

        userIdList = userIdList.stream().distinct().collect(Collectors.toList());
        List<CandidateDomain> candidateList = dealProcessNodeVO.getCandidateList();
        for (Long userId : userIdList) {
            CandidateDomain candidateDomain = new CandidateDomain();
            candidateDomain.setUserId(userId);
            candidateDomain.setUserName(userNameMap.get(userId));
            candidateDomain.setTaskId(activityInstance.getTaskId());
            candidateList.add(candidateDomain);
        }
    }

    private static void addAssignee(String assignee, String taskId, Map<Long, String> userNameMap,
                                    ProcessNodeVO dealProcessNodeVO) {
        AssigneeDomain assigneeDomain = new AssigneeDomain();
        assigneeDomain.setAssigneeId(Long.valueOf(assignee));
        assigneeDomain.setAssigneeName(userNameMap.get(assigneeDomain.getAssigneeId()));
        assigneeDomain.setTaskId(taskId);
        dealProcessNodeVO.getAssigneeList().add(assigneeDomain);
    }

    private static void addCandidateDomain(HistoricActivityInstance activityInstance, Map<Long, String> userNameMap,
                                           ProcessNodeVO dealProcessNodeVO) {
        if (StringUtils.isEmpty(activityInstance.getAssignee())) {
            return;
        }
        Long userId = Long.parseLong(activityInstance.getAssignee());
        String nickName = userNameMap.get(userId);
        CandidateDomain candidateDomain = new CandidateDomain();
        candidateDomain.setUserId(userId);
        candidateDomain.setUserName(nickName);
        candidateDomain.setTaskId(activityInstance.getTaskId());
        dealProcessNodeVO.getCandidateList().add(candidateDomain);
    }

    /**
     * 获取流程执行过程
     *
     * @param procInsId
     * @return
     */
    private ProcessViewerVO getFlowViewer(BpmnModel bpmnModel, String procInsId) {
        // 构建查询条件
        HistoricActivityInstanceQuery query =
                historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId);
        List<HistoricActivityInstance> allActivityInstanceList = query.list();
        if (CollectionUtils.isEmpty(allActivityInstanceList)) {
            return new ProcessViewerVO();
        }
        // 查询所有已完成的元素
        List<HistoricActivityInstance> finishedElementList =
                allActivityInstanceList.stream().filter(item -> ObjectUtil.isNotNull(item.getEndTime()))
                        .collect(Collectors.toList());
        // 所有已完成的连线
        Set<String> finishedSequenceFlowSet = new HashSet<>();
        // 所有已完成的任务节点
        Set<String> finishedTaskSet = new HashSet<>();
        finishedElementList.forEach(item -> {
            if (BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW.equals(item.getActivityType())) {
                finishedSequenceFlowSet.add(item.getActivityId());
            } else {
                finishedTaskSet.add(item.getActivityId());
            }
        });
        // 查询所有未结束的节点
        Set<String> unfinishedTaskSet =
                allActivityInstanceList.stream().filter(item -> ObjectUtil.isNull(item.getEndTime()))
                        .map(HistoricActivityInstance::getActivityId).collect(Collectors.toSet());
        // DFS 查询未通过的元素集合
        Set<String> rejectedSet =
                FlowableUtils.dfsFindRejects(bpmnModel, unfinishedTaskSet, finishedSequenceFlowSet, finishedTaskSet);
        Map<String, String> activityIdToNameMap = new HashMap<>();
        for (HistoricActivityInstance historicActivityInstance : allActivityInstanceList) {
            activityIdToNameMap.put(historicActivityInstance.getActivityId(),
                    historicActivityInstance.getActivityName());
        }
        return new ProcessViewerVO(finishedTaskSet, finishedSequenceFlowSet, unfinishedTaskSet, rejectedSet,
                activityIdToNameMap);
    }
}
