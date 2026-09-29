package com.wuji.workflow.controller;

import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.workflow.model.request.FlowableAuditRequest;
import com.wuji.workflow.service.WorkFlowService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflow")
public class WorkflowOperationController {

    @Autowired
    private WorkFlowService workFlowService;

    @ApiOperation("审核通过")
    @PostMapping("/taskComplete")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public void taskComplete(@RequestBody FlowableAuditRequest flowableAuditRequest) {
        workFlowService.taskComplete(flowableAuditRequest, Boolean.TRUE);
    }


    @ApiOperation("审核拒绝")
    @PostMapping("/taskReject")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public void taskReject(@RequestBody FlowableAuditRequest flowableAuditRequest) {
        workFlowService.taskReject(flowableAuditRequest);
    }

    @ApiOperation("审核驳回")
    @PostMapping("/taskReturn")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public void taskReturn(@RequestBody FlowableAuditRequest flowableAuditRequest) {
        workFlowService.taskReturn(flowableAuditRequest);
    }

    @ApiOperation("任务委派")
    @PostMapping("/delegateTask")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public void delegateTask(@RequestBody FlowableAuditRequest flowableAuditRequest) {
        workFlowService.delegateTask(flowableAuditRequest);
    }

    @ApiOperation("调整负责人")
    @PostMapping("/changeApproval")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public void changeApproval(@RequestBody FlowableAuditRequest flowableAuditRequest) {
        workFlowService.changeApproval(flowableAuditRequest);
    }

}
