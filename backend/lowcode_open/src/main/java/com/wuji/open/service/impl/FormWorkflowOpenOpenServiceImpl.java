package com.wuji.open.service.impl;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.open.converter.AbstractFormWorkflowOpenConverter;
import com.wuji.open.model.request.WorkflowCopyListRequest;
import com.wuji.open.model.request.WorkflowPendingListRequest;
import com.wuji.open.model.request.WorkflowTaskCloseOpenRequest;
import com.wuji.open.model.request.WorkflowTaskCompleteRequest;
import com.wuji.open.model.request.WorkflowTaskDelegateRequest;
import com.wuji.open.model.request.WorkflowTaskReturnRequest;
import com.wuji.open.model.vo.FlowableCommentVO;
import com.wuji.open.model.vo.FlowableDetailOpenVO;
import com.wuji.open.model.vo.PendingTaskOpenVO;
import com.wuji.open.model.vo.WorkflowCopyOpenVO;
import com.wuji.open.service.FormWorkflowOpenService;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.workflow.model.request.FlowableAuditRequest;
import com.wuji.workflow.model.request.FlowableCopyRequest;
import com.wuji.workflow.model.request.FlowableToDoListRequest;
import com.wuji.workflow.model.vo.FlowableCopyVO;
import com.wuji.workflow.model.vo.FlowableDetailVO;
import com.wuji.workflow.model.vo.FlowableOperateLogVO;
import com.wuji.workflow.model.vo.PendingTaskVO;
import com.wuji.workflow.service.FlowableCopyService;
import com.wuji.workflow.service.FlowableOperateLogService;
import com.wuji.workflow.service.WorkFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormWorkflowOpenOpenServiceImpl implements FormWorkflowOpenService {

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FlowableOperateLogService flowableOperateLogService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private FlowableCopyService flowableCopyService;

    @Override
    public List<FlowableCommentVO> workflowComment(String applicationId, String formId, String uuid) {
        LowcodeDataDomain lowcodeDataDomain = formMongoDbService.info(uuid, formId, applicationId);
        List<FlowableOperateLogVO> flowableOperateLogList =
                flowableOperateLogService.queryByInstanceId(lowcodeDataDomain.getProcessInstanceId());
        return flowableOperateLogList.stream().map(AbstractFormWorkflowOpenConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public FlowableDetailOpenVO getProcessInstanceDetail(String processInstanceId) {
        FlowableDetailVO processInstanceDetail = workFlowService.getProcessInstanceDetail(processInstanceId);
        return AbstractFormWorkflowOpenConverter.INSTANCE.toVO(processInstanceDetail);
    }

    @Override
    public void taskClose(WorkflowTaskCloseOpenRequest workflowTaskCloseRequest) {
        FlowableAuditRequest flowableAuditRequest = new FlowableAuditRequest();
        flowableAuditRequest.setProcessInstanceId(workflowTaskCloseRequest.getProcessInstanceId());
        workFlowService.taskReject(flowableAuditRequest);
    }

    @Override
    public QueryPageVO<PendingTaskOpenVO> pendingList(WorkflowPendingListRequest workflowPendingListRequest) {
        FlowableToDoListRequest flowableToDoListRequest =
                AbstractFormWorkflowOpenConverter.INSTANCE.toRequest(workflowPendingListRequest);
        QueryPageVO<PendingTaskVO> todoList = workFlowService.getTodoList(flowableToDoListRequest);
        return new QueryPageVO<>(todoList.getPageNum(), todoList.getPageSize(), todoList.getTotal(),
                todoList.getList().stream().map(AbstractFormWorkflowOpenConverter.INSTANCE::toVO)
                        .collect(Collectors.toList()));
    }

    @Override
    public void taskComplete(WorkflowTaskCompleteRequest workflowTaskCompleteRequest) {
        FlowableAuditRequest flowableAuditRequest =
                AbstractFormWorkflowOpenConverter.INSTANCE.toRequest(workflowTaskCompleteRequest);
        workFlowService.taskComplete(flowableAuditRequest, true);
    }

    @Override
    public void taskReturn(WorkflowTaskReturnRequest workflowTaskReturnRequest) {
        FlowableAuditRequest flowableAuditRequest =
                AbstractFormWorkflowOpenConverter.INSTANCE.toRequest(workflowTaskReturnRequest);
        workFlowService.taskReturn(flowableAuditRequest);
    }

    @Override
    public void delegateTask(WorkflowTaskDelegateRequest workflowTaskDelegateRequest) {
        FlowableAuditRequest flowableAuditRequest =
                AbstractFormWorkflowOpenConverter.INSTANCE.toRequest(workflowTaskDelegateRequest);
        workFlowService.delegateTask(flowableAuditRequest);
    }

    @Override
    public QueryPageVO<WorkflowCopyOpenVO> workflowCopyList(WorkflowCopyListRequest workflowCopyListRequest) {
        FlowableCopyRequest flowableCopyRequest =
                AbstractFormWorkflowOpenConverter.INSTANCE.toRequest(workflowCopyListRequest);
        QueryPageVO<FlowableCopyVO> flowableCopyVOQueryPageVO = flowableCopyService.queryList(flowableCopyRequest);
        return new QueryPageVO<>(flowableCopyVOQueryPageVO.getPageNum(), flowableCopyVOQueryPageVO.getPageSize(),
                flowableCopyVOQueryPageVO.getTotal(), flowableCopyVOQueryPageVO.getList().stream()
                        .map(AbstractFormWorkflowOpenConverter.INSTANCE::toVO).collect(Collectors.toList()));
    }

}
