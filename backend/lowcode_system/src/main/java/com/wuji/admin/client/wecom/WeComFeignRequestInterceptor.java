package com.wuji.admin.client.wecom;

import com.wuji.common.config.AbstractFeignRequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

@Slf4j
public class WeComFeignRequestInterceptor extends AbstractFeignRequestInterceptor {

    @Value("${wecom.server:https://qyapi.weixin.qq.com}")
    private String weComService;

    @Override
    protected void doApply(RequestTemplate template) {
        template.header(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    protected String serverName() {
        return "WECOM";
    }

    @Override
    protected String server() {
        return weComService;
    }
}
