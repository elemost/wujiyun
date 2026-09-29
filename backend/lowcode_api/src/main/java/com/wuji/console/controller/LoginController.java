package com.wuji.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.service.AdminMessageService;
import com.wuji.common.constant.Constants;
import com.wuji.common.model.Response;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.utils.IpUtils;
import com.wuji.framework.handler.LoginContext;
import com.wuji.framework.model.request.LoginRequest;
import com.wuji.framework.security.service.TokenService;
import com.wuji.wechat.model.vo.QrUrlVO;
import com.wuji.wechat.service.WechatMpService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private LoginContext loginContext;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AdminMessageService messageService;

    @Autowired
    private WechatMpService wechatMpService;

    @ApiOperation("登录")
    @PostMapping("")
    public Response<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest,
                                  HttpServletResponse response) {
        String ipAddr = IpUtils.getIpAddr(httpServletRequest);
        loginRequest.setIp(ipAddr);
        LoginVO login = loginContext.getHandler(loginRequest.getLoginType()).login(loginRequest, httpServletRequest);
        Cookie cookie = new Cookie(Constants.AUTH_COOKIE_NAME, login.getToken());
        cookie.setMaxAge(tokenService.getTokenExpireMinuter() * 60);
        cookie.setSecure(false);
        cookie.setPath("/");
        Cookie loginType = new Cookie(Constants.AUTH_COOKIE_LOGIN_TYPE_NAME, loginRequest.getLoginType());
        loginType.setSecure(false);
        loginType.setPath("/");
        response.addCookie(cookie);
        response.addCookie(loginType);

        Cookie suiteId = new Cookie(Constants.AUTH_COOKIE_SUITE_ID, loginRequest.getSuiteId());
        loginType.setSecure(false);
        loginType.setPath("/");
        response.addCookie(suiteId);

        Response<String> success = Response.success(login.getToken());
        success.setOtherData(JSONObject.parseObject(JSONObject.toJSONString(login)));
        return success;
    }

    @ApiOperation("切换公司登录")
    @PostMapping("/switchCompany")
    public Response<String> switchCompany(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest,
                                          HttpServletResponse response) {
        String ipAddr = IpUtils.getIpAddr(httpServletRequest);
        loginRequest.setIp(ipAddr);
        LoginVO login = loginContext.getHandler("switchCompany").login(loginRequest, httpServletRequest);
        Cookie cookie = new Cookie(Constants.AUTH_COOKIE_NAME, login.getToken());
        cookie.setMaxAge(tokenService.getTokenExpireMinuter() * 60);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
        return Response.success(login.getToken());
    }

    @ApiOperation("发送短信")
    @GetMapping(value = "/message/{mobile}")
    public void sendSms(@PathVariable String mobile) {
        messageService.sendMessage(mobile);
    }

    @ApiOperation("获取二维码")
    @GetMapping(value = "/getQrUrl")
    public QrUrlVO getQrUrl() {
        return wechatMpService.getQrUrl();
    }

}
