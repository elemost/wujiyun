package com.wuji.framework.handler.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wuji.admin.model.request.UserCreateRequest;
import com.wuji.admin.service.ThirdUserService;
import com.wuji.admin.service.UserService;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.SmsCodeUtils;
import com.wuji.framework.handler.LoginHandler;
import com.wuji.framework.handler.LoginStrategy;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.model.request.LoginRequest;
import com.wuji.framework.security.context.AuthenticationContextHolder;
import com.wuji.framework.security.handle.LoginAuthenticationToken;
import com.wuji.wechat.service.WechatMpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
@DS("slave")
public class MessageLoginHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private SmsCodeUtils smsCodeUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private ThirdUserService thirdUserService;

    @Autowired
    private WechatMpService wechatMpService;

    @Override
    public String getLoginType() {
        return "message";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {
        LoginVO loginVO = new LoginVO();
        smsCodeUtils.checkCode(loginRequest.getMobile(), loginRequest.getCode());
        UserVO user = userService.queryByMobile(loginRequest.getMobile());
        if (user == null) {
            UserCreateRequest userCreateRequest = new UserCreateRequest();
            userCreateRequest.setUserName(loginRequest.getMobile());
            userCreateRequest.setNickName(loginRequest.getMobile());
            userCreateRequest.setPhonenumber(loginRequest.getMobile());
            userCreateRequest.setStatus("0");
            userCreateRequest.setScreenCount(10);
            Long userId = userService.onlyCreateUser(userCreateRequest);
            user = new UserVO();
            user.setUserId(userId);
            loginVO.setNewUser(true);
        } else {
            if (user.getCompanyId() == null || user.getCompanyId() == 0) {
                loginVO.setNewUser(true);
            }
        }
        if (StringUtils.isNotEmpty(loginRequest.getOpenId())) {
            thirdUserService.saveOpenId(user.getUserId(), loginRequest.getOpenId(), (short) 1,
                    wechatMpService.getUserUnionId(loginRequest.getOpenId()));
        }
        Authentication authentication = null;
        try {
            authentication = authenticate(new LoginAuthenticationToken(loginRequest.getMobile(), "none"));
        } catch (Exception e) {
            log.error("账号密码错误", e);
            throw new BizException(e.getMessage());
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        LoginUserDomain loginUser = (LoginUserDomain) authentication.getPrincipal();
        setOtherInfo(loginUser);
        String token = getToken(loginUser);
        loginVO.setToken(token);

        return loginVO;
    }

}
