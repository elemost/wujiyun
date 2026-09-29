package com.wuji.open.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.ExceptionUtil;
import com.wuji.common.utils.SHA1;
import com.wuji.open.components.OpenPlatformComponent;
import com.wuji.open.model.request.ApplicationFormOpenRequest;
import com.wuji.open.model.request.ApplicationOpenRequest;
import com.wuji.open.model.request.FormOpenCommonRequest;
import com.wuji.open.model.vo.ApplicationFormOpenVO;
import com.wuji.open.model.vo.ApplicationOpenVO;
import com.wuji.open.service.ApplicationOpenService;
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
@RequestMapping("/develop/document/app")
public class ApplicationApiController {

    @Autowired
    private ApplicationOpenService applicationOpenService;

    @Autowired
    private OpenPlatformComponent openPlatformComponent;

    @ApiOperation("查询数据")
    @PostMapping("/queryList")
    public List<ApplicationOpenVO> queryList(@RequestBody ApplicationOpenRequest applicationOpenRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(applicationOpenRequest.getAppKey());
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), applicationOpenRequest.getTimestamp(),
                applicationOpenRequest.getNonce(), applicationOpenRequest.getMsgEncrypt());
        if (!sha1.equals(applicationOpenRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        applicationOpenRequest.setAppSecret(secret.getAppSecret());
        openPlatformComponent.cacheUserDomain(null, secret);
        try {
            List<ApplicationOpenVO> applicationOpenVOS = applicationOpenService.applicationList();
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(applicationOpenRequest), secret, "成功",
                    "查询应用数据", "");
            return applicationOpenVOS;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(applicationOpenRequest), secret, "失败",
                    "查询应用数据", e.getMessage() + ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }


    @ApiOperation("查询表单数据")
    @PostMapping("/form/queryList")
    public List<ApplicationFormOpenVO> queryFormList(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        openPlatformComponent.cacheUserDomain(null, secret);
        try {
            String decrypt = AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt());
            ApplicationFormOpenRequest applicationFormOpenRequest =
                    JSONObject.parseObject(decrypt, ApplicationFormOpenRequest.class);
            List<ApplicationFormOpenVO> applicationFormList =
                    applicationOpenService.applicationFormList(applicationFormOpenRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "查询表单数据", "");
            return applicationFormList;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "查询表单数据", e.getMessage() + ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }
}
