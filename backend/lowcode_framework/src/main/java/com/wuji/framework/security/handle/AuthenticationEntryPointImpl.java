package com.wuji.framework.security.handle;


import com.alibaba.fastjson.JSON;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.model.Response;
import com.wuji.framework.security.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 * 
 * @author hzm
 */
@Component
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable
{
    private static final long serialVersionUID = -8970718410437077606L;
    public static final String JWT_AUTHENTICATION_EXCEPTION = "JWT_AUTHENTICATION_EXCEPTION";
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException
    {
        Object attribute = request.getAttribute(JWT_AUTHENTICATION_EXCEPTION);
        String msg = String.format("请求访问：%s，认证失败，无法访问系统资源", request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));
        String code;
        if (ResultCode.NO_AUTH_1.getCode().equals(attribute)) {
            code = ResultCode.NO_AUTH_1.getCode();
        } else  {
            code = ResultCode.NO_AUTH.getCode();
        }
        ServletUtils.renderString(response, JSON.toJSONString(Response.failed(msg, code)));
    }
}
