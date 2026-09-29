package com.wuji.framework.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.utils.TimeUtils;
import com.wuji.framework.handler.LoginHandler;
import com.wuji.framework.handler.LoginStrategy;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.model.request.LoginRequest;
import com.wuji.framework.security.context.AuthenticationContextHolder;
import com.wuji.framework.security.handle.LoginAuthenticationToken;
import com.wuji.framework.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class SsoLoginHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private TokenService tokenService;


    @Override
    public String getLoginType() {
        return "sso";
    }

    @Override
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {
        String userInfo = tokenService.parseSSOTicket(loginRequest.getTicket(), null);
        JSONObject jsonObject = JSON.parseObject(userInfo);
        String userName = jsonObject.getString("userName");
        Object companyId = jsonObject.get("companyId");
        // 用户验证
        Authentication authentication = null;
        try {
            authentication = authenticate(new LoginAuthenticationToken(userName, "none"));
        } catch (Exception e) {
            log.error("账号密码错误", e);
            throw new AdminException(AdminResultCode.PASSWORD_ERROR);
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        LoginUserDomain loginUser = (LoginUserDomain) authentication.getPrincipal();
        Object startDate = jsonObject.get("startDate");
        Object endDate = jsonObject.get("endDate");
        if (startDate != null) {
            loginUser.setStartDate(TimeUtils.convertDate(startDate.toString()));
        }
        if (endDate != null) {
            loginUser.setEndDate(TimeUtils.convertDate(endDate.toString()));
        }
        if (companyId != null && StringUtils.isNotEmpty(companyId.toString())) {
            loginUser.setCompanyId(Long.valueOf(companyId.toString()));
        }
        // 生成token
        LoginVO loginVO = new LoginVO();
        setOtherInfo(loginUser);
        String token = getToken(loginUser);
        loginVO.setToken(token);
        return loginVO;
    }
}
