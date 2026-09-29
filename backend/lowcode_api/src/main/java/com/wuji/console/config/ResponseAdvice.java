package com.wuji.console.config;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.DingResponse;
import com.wuji.common.model.Response;
import com.wuji.common.model.WeComResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * @author Jackie
 */
@ControllerAdvice(
        basePackages = {"com.wuji.admin.controller", "com.wuji.service.controller", "com.wuji.workflow.controller",
                "com.wuji.console.controller", "com.wuji.message.controller", "com.wuji.open.controller",
                "com.wuji.wechat.controller", "com.wuji.platform.controller", "com.wuji.plugin.controller",
                "com.wuji.factory.controller"})
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType() != Response.class;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        if (returnType.getParameterType() != DingResponse.class &&
                returnType.getParameterType() != WeComResponse.class) {
            return Response.success(body);
        } else if (returnType.getParameterType() == DingResponse.class) {
            DingResponse dingResponse = JSONObject.parseObject(JSONObject.toJSONString(body), DingResponse.class);
            return dingResponse.getData();
        } else if (returnType.getParameterType() == WeComResponse.class) {
            WeComResponse dingResponse = JSONObject.parseObject(JSONObject.toJSONString(body), WeComResponse.class);
            if (dingResponse.getData() instanceof Long) {
                return Long.valueOf(dingResponse.getData().toString());
            } else {
                return String.valueOf(dingResponse.getData());
            }
        }
        return null;
    }
}
