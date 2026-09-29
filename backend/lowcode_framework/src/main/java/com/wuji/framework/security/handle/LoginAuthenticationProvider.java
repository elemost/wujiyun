package com.wuji.framework.security.handle;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Log4j2
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public LoginAuthenticationProvider(UserDetailsService userDetailsService) {
        setUserDetailsService(userDetailsService);
    }

    /**
     * 重写authentication方法，实现身份验证逻辑
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginAuthenticationToken authenticationToken = (LoginAuthenticationToken) authentication;
        String phone = (String) authenticationToken.getPrincipal();
        String loginType = (String) authenticationToken.getCredentials();
        UserDetails userDetails = null;
        //委托 UserDetailsService 查找系统用户
        userDetails = userDetailsService.loadUserByUsername(phone);
        //鉴权成功,返回一个拥有鉴权的AbstractAuthenticationToken
        LoginAuthenticationToken authenticationTokenRes =
                new LoginAuthenticationToken(userDetails, userDetails.getAuthorities());
        authenticationTokenRes.setDetails(authenticationToken.getDetails());
        return authenticationTokenRes;
    }

    /**
     * 重写supports方法，指定此AuthenticationProvider 仅支持短信验证码身份验证
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}

