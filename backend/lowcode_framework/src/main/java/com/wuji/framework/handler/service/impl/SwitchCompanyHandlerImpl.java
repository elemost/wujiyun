package com.wuji.framework.handler.service.impl;

import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.framework.handler.LoginHandler;
import com.wuji.framework.handler.LoginStrategy;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.model.request.LoginRequest;
import com.wuji.framework.security.context.AuthenticationContextHolder;
import com.wuji.framework.security.handle.LoginAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class SwitchCompanyHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private UserService userService;

    @Override
    public String getLoginType() {
        return "switchCompany";
    }

    @Override
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {

        // 用户验证
        Authentication authentication = null;
        String userName = UserUtils.getUser().getUserName();
        userService.switchCompany(UserUtils.getUser().getUserIdLongValue(), loginRequest.getCompanyId());
        try {
            authentication = authenticate(new LoginAuthenticationToken(userName, "none"));
        } catch (Exception e) {
            log.error("账号密码错误", e);
            throw new AdminException(AdminResultCode.PASSWORD_ERROR);
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        LoginUserDomain loginUser = (LoginUserDomain) authentication.getPrincipal();
        UserUtils.getUser().setCompanyId(loginRequest.getCompanyId());
        loginUser.setStartDate(TimeUtils.convertDate(UserUtils.getUser().getStartTime()));
        loginUser.setEndDate(TimeUtils.convertDate(UserUtils.getUser().getEndTime()));
        // 生成token
        LoginVO loginVO = new LoginVO();
        setOtherInfo(loginUser);
        String token = getToken(loginUser);
        loginVO.setToken(token);
        return loginVO;
    }
}
