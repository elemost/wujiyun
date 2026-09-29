package com.wuji.framework.handler.service.impl;

import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.framework.handler.LoginHandler;
import com.wuji.framework.handler.LoginStrategy;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.model.request.LoginRequest;
import com.wuji.framework.security.context.AuthenticationContextHolder;
import com.wuji.framework.security.handle.LoginAuthenticationToken;
import com.wuji.framework.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class SwitchTokenHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Override
    public String getLoginType() {
        return "switchToken";
    }

    @Override
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {
        LoginUserDomain loginUser1 = tokenService.getLoginUserDomain(loginRequest.getToken());
        // 用户验证
        Authentication authentication = null;
        UserVO userVO = userService.infoOnly(loginUser1.getUserId());
        userService.switchCompany(loginUser1.getUserId(), loginUser1.getUser().getCompanyId());
        try {
            authentication = authenticate(new LoginAuthenticationToken(userVO.getUserName(), "none"));
        } catch (Exception e) {
            log.error("账号密码错误", e);
            throw new AdminException(AdminResultCode.PASSWORD_ERROR);
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        LoginUserDomain loginUser = (LoginUserDomain) authentication.getPrincipal();
        UserDomain user = UserUtils.getUser() == null? new UserDomain() : UserUtils.getUser();
        user.setCompanyId(loginRequest.getCompanyId());
        loginUser.setStartDate(TimeUtils.convertDate(user.getStartTime()));
        loginUser.setEndDate(TimeUtils.convertDate(user.getEndTime()));
        // 生成token
        LoginVO loginVO = new LoginVO();
        setOtherInfo(loginUser);
        String token = getToken(loginUser);
        loginVO.setToken(token);
        return loginVO;
    }
}
