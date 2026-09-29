package com.wuji.framework.handler.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wuji.admin.model.vo.ThirdUserVO;
import com.wuji.admin.service.ThirdUserService;
import com.wuji.admin.service.UserService;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.framework.handler.LoginHandler;
import com.wuji.framework.handler.LoginStrategy;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.model.request.LoginRequest;
import com.wuji.framework.security.context.AuthenticationContextHolder;
import com.wuji.framework.security.handle.LoginAuthenticationToken;
import com.wuji.wechat.model.vo.WeChatMiniAppLoginVO;
import com.wuji.wechat.service.WeChatMiniAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


@Service
@Slf4j
@DS("slave")
public class WxMiniAppCodeLoginHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private WeChatMiniAppService weChatMiniAppService;

    @Autowired
    private ThirdUserService thirdUserService;

    @Autowired
    private UserService userService;

    @Override
    public String getLoginType() {
        return "miniapp_code";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {
        LoginVO loginVO = new LoginVO();
        WeChatMiniAppLoginVO loginInfo = weChatMiniAppService.getLoginInfo(loginRequest.getCode());
        String openid = loginInfo.getOpenId();
        ThirdUserVO miniAppOpenId = thirdUserService.getByOpenId(openid, (short) 2);
        UserVO user = new UserVO();
        if (miniAppOpenId == null) {
            ThirdUserVO unionId = thirdUserService.getByUnionId(openid, (short) 1);
            if (unionId == null) {
                loginVO.setNewUser(true);
            } else {
                thirdUserService.saveOpenId(unionId.getUserId(), openid, (short) 2, loginInfo.getUnionId());
                user.setUserId(unionId.getUserId());
            }
        } else {
            user.setUserId(miniAppOpenId.getUserId());
        }
        if (!loginVO.getNewUser()) {
            user = userService.infoOnly(user.getUserId());
            if (user.getCompanyId() == null || user.getCompanyId() == 0) {
                loginVO.setNewUser(true);
            }
        } else {
            if (user.getUserId() == null) {
                return loginVO;
            }
            user = userService.infoOnly(user.getUserId());
        }
        Authentication authentication;
        try {
            authentication = authenticate(new LoginAuthenticationToken(user.getUserName(), "none"));
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
