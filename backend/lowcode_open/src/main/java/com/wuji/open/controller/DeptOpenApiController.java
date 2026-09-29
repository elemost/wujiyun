package com.wuji.open.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.ExceptionUtil;
import com.wuji.common.utils.SHA1;
import com.wuji.open.components.OpenPlatformComponent;
import com.wuji.open.model.request.DeptOpenCreateRequest;
import com.wuji.open.model.request.DeptOpenDeleteRequest;
import com.wuji.open.model.request.DeptOpenUpdateRequest;
import com.wuji.open.model.request.FormOpenCommonRequest;
import com.wuji.open.model.vo.DeptOpenTreeVO;
import com.wuji.open.service.DeptOpenService;
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
@RequestMapping("/develop/document/dept")
public class DeptOpenApiController {

    @Autowired
    private DeptOpenService deptOpenService;

    @Autowired
    private OpenPlatformComponent openPlatformComponent;

    @ApiOperation("获取部门树形列表")
    @PostMapping("/tree")
    public List<DeptOpenTreeVO> tree(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(null, secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            List<DeptOpenTreeVO> tree = deptOpenService.tree();
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "获取部门树形列表", "");
            return tree;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "获取部门树形列表", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("创建部门")
    @PostMapping("/create")
    public Long create(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(null, secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            String decrypt = AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt());
            DeptOpenCreateRequest deptOpenCreateRequest = JSONObject.parseObject(decrypt, DeptOpenCreateRequest.class);
            Long create = deptOpenService.create(deptOpenCreateRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(deptOpenCreateRequest), secret, "成功",
                    "创建部门", "");
            return create;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "创建部门", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("修改部门")
    @PostMapping("/update")
    public Long update(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(null, secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            DeptOpenUpdateRequest deptOpenUpdateRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    DeptOpenUpdateRequest.class);
            Long update = deptOpenService.update(deptOpenUpdateRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(deptOpenUpdateRequest), secret, "成功",
                    "修改部门", "");
            return update;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "修改部门", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("删除部门")
    @PostMapping("/delete")
    public void delete(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(null, secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            DeptOpenDeleteRequest deptOpenDeleteRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    DeptOpenDeleteRequest.class);
            deptOpenService.delete(deptOpenDeleteRequest.getDeptId());
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "删除部门", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "删除部门", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }
}
