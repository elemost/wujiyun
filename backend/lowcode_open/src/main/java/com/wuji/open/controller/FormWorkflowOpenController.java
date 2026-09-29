package com.wuji.open.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.ExceptionUtil;
import com.wuji.common.utils.SHA1;
import com.wuji.open.components.OpenPlatformComponent;
import com.wuji.open.model.request.FormOpenCommonRequest;
import com.wuji.open.model.request.WorkflowCommentOpenRequest;
import com.wuji.open.model.request.WorkflowCopyListRequest;
import com.wuji.open.model.request.WorkflowInstanceDetailOpenRequest;
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
import com.wuji.platform.model.vo.SecretVO;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/develop/document/workflow")
public class FormWorkflowOpenController {

    @Autowired
    private FormWorkflowOpenService formWorkflowService;

    @Autowired
    private OpenPlatformComponent openPlatformComponent;

    @ApiOperation("流程评论")
    @PostMapping("/comment")
    public List<FlowableCommentVO> comment(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            String decrypt = AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt());
            WorkflowCommentOpenRequest workflowCommentOpenRequest =
                    JSONObject.parseObject(decrypt, WorkflowCommentOpenRequest.class);
            List<FlowableCommentVO> flowableCommentVOS =
                    formWorkflowService.workflowComment(workflowCommentOpenRequest.getApplicationId(),
                            workflowCommentOpenRequest.getFormId(), workflowCommentOpenRequest.getUuid());
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "流程评论", "");
            return flowableCommentVOS;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "流程评论", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("流程实例详情")
    @PostMapping("/instance/detail")
    public FlowableDetailOpenVO getProcessInstanceDetail(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            String decrypt = AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt());
            WorkflowInstanceDetailOpenRequest workflowInstanceDetailOpenRequest =
                    JSONObject.parseObject(decrypt, WorkflowInstanceDetailOpenRequest.class);
            FlowableDetailOpenVO flowableDetailOpenVO = formWorkflowService.getProcessInstanceDetail(
                    workflowInstanceDetailOpenRequest.getProcessInstanceId());
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "流程实例详情", "");
            return flowableDetailOpenVO;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "流程实例详情", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("流程任务关闭")
    @PostMapping("/taskClose")
    public void taskClose(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            String decrypt = AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt());
            WorkflowTaskCloseOpenRequest workflowTaskCloseOpenRequest =
                    JSONObject.parseObject(decrypt, WorkflowTaskCloseOpenRequest.class);
            formWorkflowService.taskClose(workflowTaskCloseOpenRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "流程任务关闭", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "流程任务关闭", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }


    @ApiOperation("流程任务列表")
    @PostMapping("/pendingList")
    public QueryPageVO<PendingTaskOpenVO> pendingList(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            String decrypt = AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt());
            WorkflowPendingListRequest workflowPendingListRequest =
                    JSONObject.parseObject(decrypt, WorkflowPendingListRequest.class);
            QueryPageVO<PendingTaskOpenVO> pendingTaskOpenVOQueryPageVO =
                    formWorkflowService.pendingList(workflowPendingListRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "流程任务列表", "");
            return pendingTaskOpenVOQueryPageVO;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "流程任务列表", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("流程任务完成")
    @PostMapping("/taskComplete")
    public void taskComplete(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            String decrypt = AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt());
            WorkflowTaskCompleteRequest workflowTaskCompleteRequest =
                    JSONObject.parseObject(decrypt, WorkflowTaskCompleteRequest.class);
            formWorkflowService.taskComplete(workflowTaskCompleteRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "流程任务完成", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "流程任务完成", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("流程任务回退")
    @PostMapping("/taskReturn")
    public void taskReturn(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            String decrypt = AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt());
            WorkflowTaskReturnRequest workflowTaskReturnRequest =
                    JSONObject.parseObject(decrypt, WorkflowTaskReturnRequest.class);
            formWorkflowService.taskReturn(workflowTaskReturnRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(workflowTaskReturnRequest), secret, "成功",
                    "流程任务回退", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "流程任务回退", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("流程任务委派")
    @PostMapping("/taskDelegate")
    public void taskDelegate(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            WorkflowTaskDelegateRequest workflowTaskDelegateRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    WorkflowTaskDelegateRequest.class);
            formWorkflowService.delegateTask(workflowTaskDelegateRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "流程任务委派", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "流程任务委派", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("流程抄送列表")
    @PostMapping("/copyList")
    public QueryPageVO<WorkflowCopyOpenVO> workflowCopyList(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            WorkflowCopyListRequest workflowCopyListRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    WorkflowCopyListRequest.class);
            QueryPageVO<WorkflowCopyOpenVO> workflowCopyOpenVOQueryPageVO =
                    formWorkflowService.workflowCopyList(workflowCopyListRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "流程抄送列表", "");
            return workflowCopyOpenVOQueryPageVO;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "流程抄送列表", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }
}
