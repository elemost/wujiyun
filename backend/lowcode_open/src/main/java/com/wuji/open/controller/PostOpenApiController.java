package com.wuji.open.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.Response;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.ExceptionUtil;
import com.wuji.common.utils.SHA1;
import com.wuji.open.components.OpenPlatformComponent;
import com.wuji.open.model.request.FormOpenCommonRequest;
import com.wuji.open.model.request.PostCreateOpenRequest;
import com.wuji.open.model.request.PostDeleteOpenRequest;
import com.wuji.open.model.request.PostSelectOpenRequest;
import com.wuji.open.model.request.PostUpdateOpenRequest;
import com.wuji.open.model.vo.PostOpenVO;
import com.wuji.open.service.PostOpenService;
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
@RequestMapping("/develop/document/post")
public class PostOpenApiController {

    @Autowired
    private OpenPlatformComponent openPlatformComponent;

    @Autowired
    private PostOpenService postOpenService;

    @ApiOperation("新增职位")
    @PostMapping("/add")
    public Response<String> addPost(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            PostCreateOpenRequest postCreateOpenRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    PostCreateOpenRequest.class);
            String code = postOpenService.addPost(postCreateOpenRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "新增职位", "");
            return Response.success(code);
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "新增职位", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("更新职位")
    @PostMapping("/update")
    public void updatePost(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            PostUpdateOpenRequest postUpdateOpenRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    PostUpdateOpenRequest.class);
            postOpenService.updatePost(postUpdateOpenRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "更新职位", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "更新职位", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("职位列表")
    @PostMapping("/list")
    public QueryPageVO<PostOpenVO> postList(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            PostSelectOpenRequest postSelectOpenRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    PostSelectOpenRequest.class);
            QueryPageVO<PostOpenVO> postList = postOpenService.postList(postSelectOpenRequest);
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "职位列表", "");
            return postList;
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "职位列表", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

    @ApiOperation("删除职位")
    @PostMapping("/delete")
    public void deletePost(@RequestBody FormOpenCommonRequest formOpenCommonRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formOpenCommonRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formOpenCommonRequest.getDataCreator(), secret);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), formOpenCommonRequest.getTimestamp(),
                formOpenCommonRequest.getNonce(), formOpenCommonRequest.getMsgEncrypt());
        if (!sha1.equals(formOpenCommonRequest.getMsgSignature())) {
            throw new ServiceException(ServiceResultCode.SIGNATURE_ERROR);
        }
        formOpenCommonRequest.setAppSecret(secret.getAppSecret());
        try {
            PostDeleteOpenRequest postSelectOpenRequest = JSONObject.parseObject(
                    AESUtils.decrypt(secret.getAppSecret().getBytes(), formOpenCommonRequest.getMsgEncrypt()),
                    PostDeleteOpenRequest.class);
            postOpenService.deletePost(postSelectOpenRequest.getPostCode());
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "成功",
                    "删除职位", "");
        } catch (Exception e) {
            openPlatformComponent.saveSecretLog(JSONObject.toJSONString(formOpenCommonRequest), secret, "失败",
                    "删除职位", ExceptionUtil.getExceptionMessage(e).substring(0, 2000));
            throw e;
        }
    }

}
