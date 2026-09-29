package com.wuji.framework.handler.service.impl;


import com.wuji.admin.constant.Constants;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.service.LoginLogService;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.utils.AESUtils;
import com.wuji.framework.handler.LoginHandler;
import com.wuji.framework.handler.LoginStrategy;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.model.request.LoginRequest;
import com.wuji.framework.security.context.AuthenticationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class PasswordLoginHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private LoginLogService loginLogService;

    @Override
    public String getLoginType() {
        return "password";
    }

    @Override
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {
        log.info("这是密码策略");
        // 用户验证
        Authentication authentication = null;
        loginRequest.setPassword(AESUtils.decrypt(Constants.PASSWORD_KEY.getBytes(), loginRequest.getPassword()));
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword());
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticate(authenticationToken);
        } catch (Exception e) {
            log.error("账号密码错误", e);
            // loginLogService.insertLog(loginRequest.getUserName(), loginRequest.getIp(), Constants.LOGIN_FAIL, AdminResultCode.PASSWORD_ERROR.getMessage());
            throw new AdminException(AdminResultCode.PASSWORD_ERROR);
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        LoginUserDomain loginUser = (LoginUserDomain) authentication.getPrincipal();

        //登录日志
        // loginLogService.insertLog(loginUser.getUsername(), loginRequest.getIp(), Constants.LOGIN_SUCCESS, "登录成功");

        // 生成token
        LoginVO loginVO = new LoginVO();
        setOtherInfo(loginUser);
        String token = getToken(loginUser);
        loginVO.setToken(token);
        loginVO.setUserId(loginUser.getUserId());
        return loginVO;
    }
}
