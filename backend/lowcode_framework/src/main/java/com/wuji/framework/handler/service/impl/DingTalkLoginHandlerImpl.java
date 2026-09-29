package com.wuji.framework.handler.service.impl;

import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DingTalkService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.framework.handler.LoginHandler;
import com.wuji.framework.handler.LoginStrategy;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.model.request.LoginRequest;
import com.wuji.framework.security.context.AuthenticationContextHolder;
import com.wuji.framework.security.handle.LoginAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DingTalkLoginHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Override
    public String getLoginType() {
        return "dingTalk";
    }

    @Override
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {
        CompanyVO companyVO = companyService.getBySecretId(loginRequest.getSecretId());
        UserDomain userDomain = new UserDomain();
        userDomain.setCompanyId(companyVO.getCompanyId());
        UserUtils.setUser(userDomain);
        String userId = dingTalkService.getUserAssessToken(loginRequest.getCode(), null, companyVO);
        List<UserPullVO> userPullVOS = dingTalkService.getUserDetailById(Collections.singletonList(userId), companyVO);
        if (CollectionUtils.isEmpty(userPullVOS)) {
            throw new AdminException(AdminResultCode.PASSWORD_ERROR);
        }
        List<UserCompanyVO> userCompanyVOList =
                userCompanyService.getByDingTalkUserId(Collections.singletonList(userId));
        if (CollectionUtils.isEmpty(userCompanyVOList)) {
            userService.saveDingUserInfo(userPullVOS, userDomain, null, null);
        }
        UserPullVO userPullVO = userPullVOS.get(0);
        // 用户验证
        Authentication authentication = null;
        try {
            authentication = authenticate(new LoginAuthenticationToken(userPullVO.getPhonenumber(), "none"));
        } catch (Exception e) {
            log.error("账号密码错误", e);
            throw new AdminException(AdminResultCode.PASSWORD_ERROR);
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        LoginUserDomain loginUser = (LoginUserDomain) authentication.getPrincipal();
        // 生成token
        loginUser.setCompanyId(companyVO.getCompanyId());
        loginUser.getUser().setCompanyId(companyVO.getCompanyId());
        LoginVO loginVO = new LoginVO();
        String token = getToken(loginUser);
        loginVO.setToken(token);
        return loginVO;
    }
}
