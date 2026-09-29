package com.wuji.open.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.ExceptionUtil;
import com.wuji.common.utils.SHA1;
import com.wuji.open.components.OpenPlatformComponent;
import com.wuji.open.model.request.FormOpenCommonRequest;
import com.wuji.open.model.request.UserDeleteOpenRequest;
import com.wuji.open.model.request.UserInfoRequest;
import com.wuji.open.model.request.UserOpenSaveRequest;
import com.wuji.open.model.request.UserOpenUpdateRequest;
import com.wuji.open.model.vo.UserOpenVO;
import com.wuji.open.service.UserOpenService;
import com.wuji.platform.model.vo.SecretVO;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/develop/document/user")
public class UserOpenApiController {

    @Autowired
    private UserOpenService userOpenService;

    @Autowired
    private OpenPlatformComponent openPlatformComponent;

    @ApiOperation("获取用户信息")
    @PostMapping("/info")
    public UserOpenVO info(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        openPlatformComponent.cacheUserDomain(null, secret);
        try {
            UserInfoRequest userInfoRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    UserInfoRequest.class);
            UserOpenVO userOpenVO = userOpenService.queryByPhone(userInfoRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "获取用户信息", "");
            return userOpenVO;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "获取用户信息", e.getMessage() + ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("创建用户")
    @PostMapping("/create")
    public void create(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        openPlatformComponent.cacheUserDomain(null, secret);
        try {
            UserOpenSaveRequest userOpenSaveRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    UserOpenSaveRequest.class);
            userOpenService.create(userOpenSaveRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "创建用户", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "创建用户", e.getMessage() + ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("修改用户")
    @PostMapping("/update")
    public void update(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        openPlatformComponent.cacheUserDomain(null, secret);
        try {
            UserOpenUpdateRequest userOpenUpdateRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    UserOpenUpdateRequest.class);
            userOpenService.update(userOpenUpdateRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "修改用户", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "修改用户", e.getMessage() + ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("删除用户")
    @PostMapping("/delete")
    public void delete(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        openPlatformComponent.cacheUserDomain(null, secret);
        try {
            UserDeleteOpenRequest userOpenUpdateRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    UserDeleteOpenRequest.class);
            userOpenService.delete(userOpenUpdateRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "删除用户", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "删除用户", e.getMessage() + ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }
}
