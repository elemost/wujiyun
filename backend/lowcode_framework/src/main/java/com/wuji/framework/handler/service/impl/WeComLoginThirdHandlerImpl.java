package com.wuji.framework.handler.service.impl;

import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.admin.service.WeComThirdService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.LoginVO;
import com.wuji.common.model.vo.UserVO;
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
public class WeComLoginThirdHandlerImpl extends LoginHandler implements LoginStrategy {

    @Autowired
    private WeComThirdService weComThirdServiceImpl;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Override
    public String getLoginType() {
        return "weComThird";
    }

    @Override
    public LoginVO login(LoginRequest loginRequest, HttpServletRequest request) {
        CompanyVO companyVO = companyService.info(loginRequest.getCompanyId());
        UserDomain userDomain = new UserDomain();
        userDomain.setCompanyId(companyVO.getCompanyId());
        userDomain.setSuiteId(loginRequest.getSuiteId());
        UserUtils.setUser(userDomain);
        List<UserCompanyVO> userCompanyVOList =
                userCompanyService.getByWeComUserId(Collections.singletonList(loginRequest.getUserId()));
        // 用户验证
        Authentication authentication;
        if (CollectionUtils.isNotEmpty(userCompanyVOList)) {
            try {
                UserCompanyVO userCompanyVO = userCompanyVOList.get(0);
                userService.switchCompany(userCompanyVO.getUserId(), loginRequest.getCompanyId());
                List<UserVO> userVOS = userService.queryByIds(Collections.singletonList(userCompanyVO.getUserId()));
                authentication = authenticate(new LoginAuthenticationToken(userVOS.get(0).getUserName(), "weCom"));
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
            loginUser.setSuiteId(loginRequest.getSuiteId());
            LoginVO loginVO = new LoginVO();
            String token = getToken(loginUser);
            loginVO.setToken(token);
            return loginVO;
        }
        // checkCode(loginRequest.getMobile(), loginRequest.getCode());
        List<UserPullVO> userPullVOS =
                weComThirdServiceImpl.getUserDetailById(Collections.singletonList(loginRequest.getUserId()), companyVO);
        if (CollectionUtils.isEmpty(userPullVOS)) {
            throw new AdminException(AdminResultCode.PASSWORD_ERROR);
        }

        // UserPullVO userPullVO = userPullVOS.get(0);
        // userPullVO.setUserName(loginRequest.getMobile());
        // userPullVO.setPhonenumber(loginRequest.getMobile());
        // UserVO userVO = userService.queryByMobile(loginRequest.getMobile());
        // if (userVO != null) {
        //     UserCompanyVO userCompanyVO =
        //             userCompanyService.getByUserIdAndCompanyId(userVO.getUserId(), UserUtils.getUser().getCompanyId());
        //     if (userCompanyVO == null) {
        //         userPullVO.setNickName(loginRequest.getMobile());
        //     } else {
        //         userPullVO.setNickName(userCompanyVO.getNickName());
        //     }
        // }
        // userCompanyService.getByWeComUserId(Collections.singletonList(loginRequest.getUserId()));
        userService.saveDingUserInfo(userPullVOS, userDomain, null, null);

        try {
            userCompanyVOList =
                    userCompanyService.getByWeComUserId(Collections.singletonList(loginRequest.getUserId()));
            UserCompanyVO userCompanyVO = userCompanyVOList.get(0);
            userCompanyService.setAdmin(userCompanyVO.getCompanyId(), userCompanyVO.getUserId());
            userService.switchCompany(userCompanyVO.getUserId(), loginRequest.getCompanyId());
            List<UserVO> userVOS = userService.queryByIds(Collections.singletonList(userCompanyVO.getUserId()));
            authentication = authenticate(new LoginAuthenticationToken(userVOS.get(0).getUserName(), "weCom"));
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
        loginUser.setSuiteId(loginRequest.getSuiteId());
        LoginVO loginVO = new LoginVO();
        String token = getToken(loginUser);
        loginVO.setToken(token);
        return loginVO;
    }
}
