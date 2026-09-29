package com.wuji.open.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.ExceptionUtil;
import com.wuji.common.utils.SHA1;
import com.wuji.open.components.OpenPlatformComponent;
import com.wuji.open.model.request.FormDataQueryRequest;
import com.wuji.open.model.request.FormOpenCommonRequest;
import com.wuji.open.model.request.FormSyncOpenRequest;
import com.wuji.open.model.vo.FormDataSyncVO;
import com.wuji.open.service.FormOpenService;
import com.wuji.platform.model.vo.SecretVO;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.vo.LowcodeDataVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/develop/document/form")
public class FormOpenApiController {

    @Autowired
    private FormOpenService formOpenService;

    @Autowired
    private OpenPlatformComponent openPlatformComponent;

    @ApiOperation("插入数据")
    @PostMapping("/data/insert")
    public FormDataSyncVO insertData(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            FormSyncOpenRequest formSyncOpenRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    FormSyncOpenRequest.class);
            FormDataSyncVO sync = formOpenService.insert(formSyncOpenRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "新增表单数据", "");
            return sync;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "新增表单数据", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("修改数据")
    @PostMapping("/data/update")
    public FormDataSyncVO updateData(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            FormSyncOpenRequest formSyncOpenRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    FormSyncOpenRequest.class);
            FormDataSyncVO sync = formOpenService.update(formSyncOpenRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "修改表单数据", "");
            return sync;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "修改表单数据", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("删除数据")
    @PostMapping("/data/delete")
    public FormDataSyncVO deleteData(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            FormSyncOpenRequest formSyncOpenRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    FormSyncOpenRequest.class);
            FormDataSyncVO sync = formOpenService.delete(formSyncOpenRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "删除表单数据", "");
            return sync;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "删除表单数据", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }


    @ApiOperation("查询数据")
    @PostMapping("/queryList")
    public QueryPageVO<LowcodeDataVO> queryList(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        openPlatformComponent.cacheUserDomain(null, secret);
        try {
            FormDataQueryRequest formDataQueryRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    FormDataQueryRequest.class);
            QueryPageVO<LowcodeDataVO> lowcodeDataVOQueryPageVO = formOpenService.queryList(formDataQueryRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "查询数据", "");
            return lowcodeDataVOQueryPageVO;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "查询数据", e.getMessage() + ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

}
