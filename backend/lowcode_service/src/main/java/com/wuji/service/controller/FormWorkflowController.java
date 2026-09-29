package com.wuji.service.controller;


import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.common.model.Response;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.model.request.FormFlowableCopyRequest;
import com.wuji.service.model.request.FormFlowableDoneListRequest;
import com.wuji.service.model.request.FormFlowableOwnerListRequest;
import com.wuji.service.model.request.FormFlowableToDoListRequest;
import com.wuji.service.model.request.WorkFlowBatchAuditRequest;
import com.wuji.service.model.vo.FormDoneTaskVO;
import com.wuji.service.model.vo.FormFlowableCopyVO;
import com.wuji.service.model.vo.FormOwnerTaskVO;
import com.wuji.service.model.vo.FormPendingCountVO;
import com.wuji.service.model.vo.FormPendingTaskVO;
import com.wuji.service.service.FormWorkflowService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/form/workflow")
public class FormWorkflowController {

    @Autowired
    private FormWorkflowService formWorkflowService;

    @ApiOperation("代办列表")
    @PostMapping("/getPendingList")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public QueryPageVO<FormPendingTaskVO> getPendingList(
            @RequestBody FormFlowableToDoListRequest flowableToDoListRequest) {
        return formWorkflowService.getPendingList(flowableToDoListRequest);
    }

    @ApiOperation("代办数量")
    @PostMapping("/getFormPendingCount")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public List<FormPendingCountVO> getFormPendingCount(
            @RequestBody FormFlowableToDoListRequest flowableToDoListRequest) {
        return formWorkflowService.getFormPendingCount(flowableToDoListRequest);
    }

    @ApiOperation("代办列表")
    @GetMapping("/getPendingInfo/{taskId}")
    @CorpCoopResource(location = IdentifierLocationEnum.REQ_PRAM)
    public FormPendingTaskVO getPendingList(@PathVariable String taskId,
                                            @RequestParam(value = "companyUuid", required = false) @CorpCoop
                                            String companyUuid) {
        FormFlowableToDoListRequest flowableToDoListRequest = new FormFlowableToDoListRequest();
        flowableToDoListRequest.setTaskId(taskId);
        QueryPageVO<FormPendingTaskVO> pendingList = formWorkflowService.getPendingList(flowableToDoListRequest);
        if (CollectionUtils.isNotEmpty(pendingList.getList())) {
            return pendingList.getList().get(0);
        } else {
            return null;
        }
    }

    @ApiOperation("待办数量")
    @PostMapping("/pendingCount")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public Integer pendingCount(@RequestBody FormFlowableToDoListRequest flowableToDoListRequest) {
        return formWorkflowService.pendingCount(flowableToDoListRequest);
    }


    @ApiOperation("已办列表")
    @PostMapping("/getDoneList")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public QueryPageVO<FormDoneTaskVO> getDoneList(
            @RequestBody FormFlowableDoneListRequest formFlowableDoneListRequest) {
        return formWorkflowService.getDoneList(formFlowableDoneListRequest);
    }

    @ApiOperation("已办详情")
    @GetMapping("/getDoneInfo/{taskId}")
    @CorpCoopResource(location = IdentifierLocationEnum.REQ_PRAM)
    public FormDoneTaskVO getDoneInfo(@PathVariable String taskId,
                                      @RequestParam(value = "companyUuid", required = false) @CorpCoop
                                      String companyUuid) {
        FormFlowableDoneListRequest formFlowableDoneListRequest = new FormFlowableDoneListRequest();
        formFlowableDoneListRequest.setTaskId(taskId);
        QueryPageVO<FormDoneTaskVO> doneList = formWorkflowService.getDoneList(formFlowableDoneListRequest);
        if (CollectionUtils.isNotEmpty(doneList.getList())) {
            return doneList.getList().get(0);
        } else {
            return null;
        }
    }

    @ApiOperation("自己发起列表")
    @PostMapping("/getOwnerList")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public QueryPageVO<FormOwnerTaskVO> getOwnerList(
            @RequestBody FormFlowableOwnerListRequest formFlowableOwnerListRequest) {
        return formWorkflowService.getOwnerList(formFlowableOwnerListRequest);
    }

    @ApiOperation("自己发起详情")
    @GetMapping("/getOwnerInfo/{processInstanceId}")
    @CorpCoopResource(location = IdentifierLocationEnum.REQ_PRAM)
    public FormOwnerTaskVO getOwnerInfo(@PathVariable String processInstanceId,
                                        @RequestParam(value = "companyUuid", required = false) @CorpCoop
                                        String companyUuid) {
        FormFlowableOwnerListRequest formFlowableOwnerListRequest = new FormFlowableOwnerListRequest();
        formFlowableOwnerListRequest.setProcessInstanceId(processInstanceId);
        QueryPageVO<FormOwnerTaskVO> ownerList = formWorkflowService.getOwnerList(formFlowableOwnerListRequest);
        if (CollectionUtils.isNotEmpty(ownerList.getList())) {
            return ownerList.getList().get(0);
        } else {
            return null;
        }
    }

    @ApiOperation("获取抄送列表")
    @PostMapping("/getCopyList")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public QueryPageVO<FormFlowableCopyVO> getCopyList(@RequestBody FormFlowableCopyRequest formFlowableCopyRequest) {
        return formWorkflowService.getCopyList(formFlowableCopyRequest);
    }

    @ApiOperation("自己发起详情")
    @GetMapping("/getCopyInfo/{id}")
    @CorpCoopResource(location = IdentifierLocationEnum.REQ_PRAM)
    public FormFlowableCopyVO getCopyInfo(@PathVariable Long id,
                                          @RequestParam(value = "companyUuid", required = false) @CorpCoop
                                          String companyUuid) {
        FormFlowableCopyRequest formFlowableCopyRequest = new FormFlowableCopyRequest();
        formFlowableCopyRequest.setId(id);
        QueryPageVO<FormFlowableCopyVO> copyList = formWorkflowService.getCopyList(formFlowableCopyRequest);
        if (CollectionUtils.isNotEmpty(copyList.getList())) {
            return copyList.getList().get(0);
        } else {
            return null;
        }
    }

    @ApiOperation("批量审批")
    @PostMapping("/batchComplete")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public Response<String> batchComplete(@RequestBody WorkFlowBatchAuditRequest workFlowBatchAuditRequest) {
        return Response.success(formWorkflowService.batchComplete(workFlowBatchAuditRequest));
    }


    @ApiOperation("批量驳回")
    @PostMapping("/batchReject")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public Response<String> batchReject(@RequestBody WorkFlowBatchAuditRequest workFlowBatchAuditRequest) {
        return Response.success(formWorkflowService.batchReject(workFlowBatchAuditRequest));
    }
}
