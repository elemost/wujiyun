package com.wuji.workflow.service;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.workflow.model.request.FlowableAuditRequest;
import com.wuji.workflow.model.request.FlowableDoneListRequest;
import com.wuji.workflow.model.request.FlowableOwnerListRequest;
import com.wuji.workflow.model.request.FlowableToDoListRequest;
import com.wuji.workflow.model.vo.DoneTaskVO;
import com.wuji.workflow.model.vo.FlowableDetailVO;
import com.wuji.workflow.model.vo.OwnerTaskVO;
import com.wuji.workflow.model.vo.PendingTaskVO;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface WorkFlowService {
    /**
     * 创建流程
     *
     * @param deploymentId 流程引擎业务类型
     * @param userId       创建人
     * @param variables    动态参数
     * @return
     */
    String createTask(String deploymentId, String userId, HashMap<String, Object> variables);

    void finishFirstAudit(String processInstanceId);

    /**
     * 审批通过
     *
     * @param flowableAuditRequest
     * @param needAddOperate
     */
    void taskComplete(FlowableAuditRequest flowableAuditRequest, Boolean needAddOperate);


    /**
     * 审核拒绝
     *
     * @param flowableAuditRequest
     */
    void taskReject(FlowableAuditRequest flowableAuditRequest);

    /**
     * 退回任务
     *
     * @param flowableAuditRequest 请求实体参数
     */
    void taskReturn(FlowableAuditRequest flowableAuditRequest);

    /**
     * 委派任务
     *
     * @param flowableAuditRequest
     */
    void delegateTask(FlowableAuditRequest flowableAuditRequest);

    /**
     * 修改当前节点任务人
     *
     * @param flowableAuditRequest
     */
    void changeApproval(FlowableAuditRequest flowableAuditRequest);

    /**
     * 转办任务
     *
     * @param flowableAuditRequest 请求实体参数
     */
    void transferTask(FlowableAuditRequest flowableAuditRequest);

    /**
     * 任务暂存
     *
     * @param flowableAuditRequest
     */
    void taskClaim(FlowableAuditRequest flowableAuditRequest);

    /**
     * 自己发起的流程
     *
     * @param flowableOwnerListRequest
     * @return
     */
    QueryPageVO<OwnerTaskVO> getOwnerList(FlowableOwnerListRequest flowableOwnerListRequest);

    List<PendingTaskVO> getTaskByProcessInstanceIdList(List<String> processInstanceIdList);

    /**
     * 待办列表
     *
     * @param flowableToDoListRequest
     * @return
     */
    QueryPageVO<PendingTaskVO> getTodoList(FlowableToDoListRequest flowableToDoListRequest);

    QueryPageVO<PendingTaskVO> getTodoListOnlyTask(FlowableToDoListRequest flowableToDoListRequest);

    List<String> getProcessInstanceIdListByTaskId(List<String> taskKeys, String processDefinitionKey);

    List<String> getProcessInstanceIdListByAssigneeId(List<String> assigneeIds, String processDefinitionKey);

    /**
     * 已办列表
     *
     * @param flowableDoneListRequest
     * @return
     */
    QueryPageVO<DoneTaskVO> getDoneList(FlowableDoneListRequest flowableDoneListRequest);

    /**
     * 获取实例详情
     *
     * @param processInstanceId
     * @return
     */
    FlowableDetailVO getProcessInstanceDetail(String processInstanceId);

    void terminateByParent(String parentProcessInstanceId);

    void updateVariable(String processInstanceId, Map<String, Object> variable);

    void copy(HistoricTaskInstance task, Map<String, Object> processVariables, JSONObject jsonObject);

    void deleteDataByProcessDefinitionKey(String processDefinitionKey);
}
