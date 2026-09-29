package com.wuji.framework.handler;


import com.wuji.admin.cache.CompanyPullConfigCache;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.constant.Constants;
import com.wuji.common.model.vo.CompanyPullConfigVO;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Slf4j
@Component
public class LoginHandler {

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CompanyService companyService;


    public Authentication authenticate(Authentication authentication) {
        return authenticationManager.authenticate(authentication);
    }

    public String getToken(LoginUserDomain loginUser) {
        return tokenService.createToken(loginUser, null);
    }

    public void setOtherInfo(LoginUserDomain loginUserDomain) {
        CompanyVO info = companyService.info(loginUserDomain.getUser().getCompanyId());
        if (CompanyDataSourceEnum.WECOM_THIRD.name().equals(info.getDataSource())) {
            CompanyPullConfigVO config = CompanyPullConfigCache.getValue(loginUserDomain.getUser().getCompanyId(),
                    Constants.getDefaultSuiteId());
            if (StringUtils.isEmpty(loginUserDomain.getSuiteId()) && StringUtils.isNotEmpty(config.getAppId())) {
                loginUserDomain.setSuiteId(Constants.getDefaultSuiteId());
            }
        }
    }
}
