package com.wuji.console.filter;

import com.wuji.admin.service.CompanyInfoService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AnonymousFilter implements Filter {

    private CompanyInfoService companyInfoService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        developCheck(requestURI, httpServletRequest);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void developCheck(String requestURI, HttpServletRequest httpServletRequest) {
        // if (requestURI.contains("/develop/document/")) {
        //     String authHeader = httpServletRequest.getHeader("Authorization");
        //     if (authHeader != null && authHeader.startsWith("Bearer ")) {
        //         if (companyInfoService == null) {
        //             companyInfoService = ToolSpring.getBean(CompanyInfoService.class);
        //         }
        //         String token = authHeader.substring(7); // 去掉 "Bearer " 前缀
        //         CompanyInfoVO companyInfoVO =
        //                 companyInfoService.getByKeyAndValue(CompanyInfoKeyEnum.COMPANY_KEY.name(), token);
        //         if (companyInfoVO == null) {
        //             throw new BizException(ResultCode.NO_AUTH);
        //         }
        //         UserDomain userDomain = new UserDomain();
        //         userDomain.setCompanyId(companyInfoVO.getCompanyId());
        //         UserUtils.setUser(userDomain);
        //     } else {
        //         throw new BizException(ResultCode.NO_AUTH);
        //     }
        // }
    }
}
