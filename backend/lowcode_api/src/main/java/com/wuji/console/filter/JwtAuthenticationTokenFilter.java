package com.wuji.console.filter;


import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.wuji.admin.cache.UserCompanyCache;
import com.wuji.admin.components.CorpCoopComponent;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.IpUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.security.service.TokenService;
import com.wuji.service.cache.ApplicationCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    public static final String JWT_AUTHENTICATION_EXCEPTION = "JWT_AUTHENTICATION_EXCEPTION";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        LoginUserDomain loginUser = tokenService.getLoginUser(request);
        if (ObjectUtils.isNotEmpty(loginUser) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String ipAddr = IpUtils.getIpAddr(request);
            tokenService.verifyToken(loginUser);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            String applicationId = request.getHeader("Application");
            String suiteId = loginUser.getSuiteId();
            String requestURI = request.getRequestURI();
            // if (Constants.checkUrl(requestURI, loginUser.getCompanyId())) {
            //     throw new BizException(ResultCode.CAN_NOT_OPERATOR);
            // }
            if (StringUtils.isNotEmpty(applicationId)) {
                Long companyId = ApplicationCache.getCompanyId(applicationId);
                if (companyId == null) {
                    throw new BizException(ResultCode.NO_AUTH_APP);
                }
                if (Objects.equals(companyId, loginUser.getUser().getCompanyId())) {
                    setUserInfo(loginUser, ipAddr, suiteId);
                } else {
                    UserDomain userDomain = new UserDomain();
                    userDomain.setCompanyId(companyId);
                    userDomain.setUserId(loginUser.getUserId().toString());
                    userDomain.setSuiteId(suiteId);
                    UserUtils.setUser(userDomain);
                    UserVO userVO = UserCompanyCache.getUserById(companyId + "_" + loginUser.getUserId());
                    if (userVO == null) {
                        request.setAttribute(JWT_AUTHENTICATION_EXCEPTION, ResultCode.NO_AUTH_1.getCode());
                        throw new BizException(ResultCode.NO_AUTH_1);
                    }
                    CorpCoopComponent.buildUserDomain(userVO);
                }
            } else {
                setUserInfo(loginUser, ipAddr, suiteId);
            }
        }
        chain.doFilter(request, response);
    }

    private static void setUserInfo(LoginUserDomain loginUser, String ipAddr, String suiteId) {
        UserVO user = loginUser.getUser();
        UserDomain userDomain = new UserDomain();
        userDomain.setUserId(loginUser.getUserId().toString());
        userDomain.setUserIdLongValue(loginUser.getUserId());
        userDomain.setCompanyId(user.getCompanyId());
        userDomain.setUserName(loginUser.getUsername());
        userDomain.setNickName(user.getNickName());
        userDomain.setRealName(user.getRealName());
        userDomain.setIp(ipAddr);
        if (CollectionUtils.isNotEmpty(user.getDeptIdList())) {
            userDomain.setDeptIdList(user.getDeptIdList());
        } else {
            userDomain.setDeptIdList(new ArrayList<>());
        }
        if (CollectionUtils.isNotEmpty(user.getDepartmentList())) {
            userDomain.setDataScopeDeptIdList(
                    user.getDepartmentList().stream().map(DepartmentVO::getDeptId).collect(Collectors.toList()));
        } else {
            userDomain.setDataScopeDeptIdList(Lists.newArrayList());
        }
        // 全公司时部门id为0
        userDomain.getDataScopeDeptIdList().add(0L);
        userDomain.setPostIdList(user.getPostIdList());
        userDomain.setUserDeptList(user.getUserDeptList());
        userDomain.setCompanyUuid(user.getCompanyUuid());
        if (!Objects.isNull(loginUser.getStartDate())) {
            userDomain.setStartTime(DateUtil.formatDateTime(loginUser.getStartDate()));
        }
        if (!Objects.isNull(loginUser.getEndDate())) {
            userDomain.setEndTime(DateUtil.formatDateTime(loginUser.getEndDate()));
        }
        userDomain.setUserType(user.getUserType());
        userDomain.setAdminUser(user.getAdminUser());
        userDomain.setSuiteId(suiteId);
        UserUtils.setUser(userDomain);
    }

}
