package com.wuji.open.converter;

import com.wuji.open.model.request.WorkflowCopyListRequest;
import com.wuji.open.model.request.WorkflowPendingListRequest;
import com.wuji.open.model.request.WorkflowTaskCompleteRequest;
import com.wuji.open.model.request.WorkflowTaskDelegateRequest;
import com.wuji.open.model.request.WorkflowTaskReturnRequest;
import com.wuji.open.model.vo.FlowableCommentVO;
import com.wuji.open.model.vo.FlowableDetailOpenVO;
import com.wuji.open.model.vo.PendingTaskOpenVO;
import com.wuji.open.model.vo.WorkflowCopyOpenVO;
import com.wuji.workflow.model.request.FlowableAuditRequest;
import com.wuji.workflow.model.request.FlowableCopyRequest;
import com.wuji.workflow.model.request.FlowableToDoListRequest;
import com.wuji.workflow.model.vo.FlowableCopyVO;
import com.wuji.workflow.model.vo.FlowableDetailVO;
import com.wuji.workflow.model.vo.FlowableOperateLogVO;
import com.wuji.workflow.model.vo.PendingTaskVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormWorkflowOpenConverter {

    public static final AbstractFormWorkflowOpenConverter INSTANCE =
            Mappers.getMapper(AbstractFormWorkflowOpenConverter.class);

    public abstract FlowableCommentVO toVO(FlowableOperateLogVO flowableOperateLogVO);

    public abstract FlowableDetailOpenVO toVO(FlowableDetailVO flowableDetailVO);

    @Mapping(source = "applicationId", target = "category")
    public abstract FlowableToDoListRequest toRequest(WorkflowPendingListRequest workflowPendingListRequest);

    public abstract PendingTaskOpenVO toVO(PendingTaskVO pendingTaskVO);

    public abstract FlowableAuditRequest toRequest(WorkflowTaskCompleteRequest workflowTaskCompleteRequest);

    public abstract FlowableAuditRequest toRequest(WorkflowTaskReturnRequest workflowTaskReturnRequest);

    public abstract FlowableAuditRequest toRequest(WorkflowTaskDelegateRequest workflowTaskDelegateRequest);

    public abstract FlowableCopyRequest toRequest(WorkflowCopyListRequest workflowCopyListRequest);

    public abstract WorkflowCopyOpenVO toVO(FlowableCopyVO flowableCopyVO);
}
