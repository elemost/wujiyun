package com.wuji.open.service;

import com.wuji.common.model.vo.QueryPageVO;
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

import java.util.List;

public interface FormWorkflowOpenService {

    List<FlowableCommentVO> workflowComment(String applicationId, String formId, String uuid);

    FlowableDetailOpenVO getProcessInstanceDetail(String processInstanceId);

    void taskClose(WorkflowTaskCloseOpenRequest workflowTaskCloseRequest);

    QueryPageVO<PendingTaskOpenVO> pendingList(WorkflowPendingListRequest workflowPendingListRequest);

    void taskComplete(WorkflowTaskCompleteRequest workflowTaskCompleteRequest);

    void taskReturn(WorkflowTaskReturnRequest workflowTaskReturnRequest);

    void delegateTask(WorkflowTaskDelegateRequest workflowTaskDelegateRequest);

    QueryPageVO<WorkflowCopyOpenVO> workflowCopyList(WorkflowCopyListRequest workflowCopyListRequest);
}
