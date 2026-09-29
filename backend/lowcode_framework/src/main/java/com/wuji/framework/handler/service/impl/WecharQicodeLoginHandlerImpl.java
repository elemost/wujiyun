package com.wuji.framework.handler.service.impl;

import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.vo.SendSmsVO;
import com.wuji.admin.model.vo.ThirdUserVO;
import com.wuji.admin.model.vo.UserQicodeLoginVO;
import com.wuji.admin.service.ThirdUserService;
import com.wuji.admin.service.UserQicodeLoginService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.common.utils.redis.RedisCache;
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

@Service
@Slf4j
public class WecharQicodeLoginHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserService userService;

    @Autowired
    private ThirdUserService thirdUserService;

    @Autowired
    private UserQicodeLoginService userQicodeLoginService;

    @Override
    public String getLoginType() {
        return "qicode";
    }

    @Override
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {
        final UserQicodeLoginVO qicodeLogin = userQicodeLoginService.getByTicket(loginRequest.getTicket());
        if (qicodeLogin == null) {
            return new LoginVO();
        }
        LoginVO loginVO = new LoginVO();
        ThirdUserVO thirdUserVO = thirdUserService.getByOpenId(qicodeLogin.getOpenId(), (short) 1);
        if (thirdUserVO != null) {
            UserVO user = userService.info(thirdUserVO.getId());
            if (user == null) {
                loginVO.setQiCodeNewUser(true);
            } else {
                loginVO.setQiCodeNewUser(false);
                Authentication authentication = null;
                try {
                    authentication =
                            authenticate(new LoginAuthenticationToken(UserUtils.getUser().getUserName(), "none"));
                } catch (Exception e) {
                    log.error("账号密码错误", e);
                    throw new AdminException(AdminResultCode.PASSWORD_ERROR);
                } finally {
                    AuthenticationContextHolder.clearContext();
                }
                LoginUserDomain loginUser = (LoginUserDomain) authentication.getPrincipal();
                userQicodeLoginService.used(qicodeLogin.getId());
                String token = getToken(loginUser);
                loginVO.setToken(token);
            }
        } else {
            loginVO.setQiCodeNewUser(true);
        }
        return loginVO;
    }

    /**
     * 检查验证码是否正确
     *
     * @param mobile
     * @param code
     * @return
     */
    private boolean checkVerificationCode(String mobile, String code) {
        SendSmsVO sendSmsVO = redisCache.getCacheObject(mobile);
        if (sendSmsVO != null) {
            String cacheCode = sendSmsVO.getCode();
            if (cacheCode == null || !cacheCode.equals(code)) {
                return false;
            }
            Long checkCreateTime = sendSmsVO.getCreateTime();
            if (checkCreateTime < System.currentTimeMillis() + 10 * 60 * 1000) {
                return true;
            }
        }
        return false;
    }
}
