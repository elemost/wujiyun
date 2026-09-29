package com.wuji.service.client;

import com.wuji.common.config.AbstractFeignRequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

@Slf4j
public class PluginFeignRequestInterceptor extends AbstractFeignRequestInterceptor {

    @Value("${plugin.server:http://127.0.0.1:9010}")
    private String pluginServer;

    @Override
    protected void doApply(RequestTemplate template) {
        template.header(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    }

    @Override
    protected String serverName() {
        return "PLUGIN";
    }

    @Override
    protected String server() {
        return pluginServer;
    }
}
