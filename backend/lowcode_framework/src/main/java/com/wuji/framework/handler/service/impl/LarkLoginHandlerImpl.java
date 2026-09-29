package com.wuji.framework.handler.service.impl;

import com.wuji.admin.client.lark.model.UserLoginVO;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.LarkService;
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
public class LarkLoginHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private LarkService larkService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Override
    public String getLoginType() {
        return "lark";
    }

    @Override
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {
        CompanyVO companyVO = companyService.getBySecretId(loginRequest.getSecretId());
        UserDomain userDomain = new UserDomain();
        userDomain.setCompanyId(companyVO.getCompanyId());
        UserUtils.setUser(userDomain);
        String userAssessToken =
                larkService.getUserAssessToken(loginRequest.getCode(), loginRequest.getUrl(), companyVO);
        UserLoginVO userLoginVO = larkService.getUserInfoByAssessToken(userAssessToken);
        if (userLoginVO.getMobile().contains("+86")) {
            userLoginVO.setMobile(userLoginVO.getMobile().replace("+86", ""));
        } else {
            userLoginVO.setMobile(userLoginVO.getMobile());
        }
        List<UserCompanyVO> userCompanyVOList =
                userCompanyService.getByLarkUserId(Collections.singletonList(userLoginVO.getUser_id()));
        if (CollectionUtils.isEmpty(userCompanyVOList)) {
            List<UserPullVO> userPullVOS =
                    larkService.getUserDetailById(Collections.singletonList(userLoginVO.getUser_id()), companyVO);
            userService.saveDingUserInfo(userPullVOS, userDomain, null, null);
        }
        // 用户验证
        Authentication authentication = null;
        try {
            authentication = authenticate(new LoginAuthenticationToken(userLoginVO.getMobile(), "none"));
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
