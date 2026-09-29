package com.wuji.console.controller;


import com.wuji.common.constant.Constants;
import com.wuji.common.model.Response;
import com.wuji.framework.handler.LoginContext;
import com.wuji.framework.model.request.LoginRequest;
import com.wuji.framework.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("")
@Slf4j
public class SsoLoginController {

    @Autowired
    private LoginContext loginContext;

    @Autowired
    private TokenService tokenService;

    @Value("${sso.oauth.authorizeUrl}")
    private String ssoAuthorizeUrl;

    @Value("${sso.client.id:elecloud}")
    private String ssoClientId;

    @Value("${sso.oauth.backUrl}")
    private String ssoDefaultBackUrl;

    @GetMapping("/sso/login")
    public Response<String> ssoLogin(@RequestParam(value = "ticket", required = false) String ticket,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setTicket(ticket);
        String redirectUrl = request.getParameter("redirect_url");
        if (StringUtils.isEmpty(redirectUrl)) {
            redirectUrl = ssoDefaultBackUrl;
        }
        // 没有ticket时跳转到登录页面
        if (StringUtils.isEmpty(ticket) || "null".equalsIgnoreCase(ticket)) {
            response.sendRedirect(ssoAuthorizeUrl + "?client_id=" + ssoClientId + "&redirect_url=" + redirectUrl);
            return null;
        }
        String login = loginContext.getHandler("sso").login(loginRequest, request).getToken();

        // 登录成功跳转到重定向页面
        Cookie cookie = new Cookie(Constants.AUTH_COOKIE_NAME, login);
        cookie.setMaxAge(tokenService.getTokenExpireMinuter() * 60);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.sendRedirect(redirectUrl);
        return Response.success(login);
    }

    @GetMapping("/system/sso/login")
    public Response<String> systemSsoLogin(@RequestParam(value = "ticket", required = false) String ticket,
                                           HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setTicket(ticket);
        String redirectUrl = request.getParameter("redirect_url");
        if (StringUtils.isEmpty(redirectUrl)) {
            redirectUrl = ssoDefaultBackUrl;
        }
        // 没有ticket时跳转到登录页面
        if (StringUtils.isEmpty(ticket) || "null".equalsIgnoreCase(ticket)) {
            response.sendRedirect(ssoAuthorizeUrl + "?client_id=" + ssoClientId + "&redirect_url=" + redirectUrl);
            return null;
        }
        String login = loginContext.getHandler("systemSso").login(loginRequest, request).getToken();

        // 登录成功跳转到重定向页面
        Cookie cookie = new Cookie(Constants.AUTH_COOKIE_NAME, login);
        cookie.setMaxAge(tokenService.getTokenExpireMinuter() * 60);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.sendRedirect(redirectUrl);
        return Response.success(login);
    }
}
