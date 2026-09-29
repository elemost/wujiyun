package com.wuji.admin.client.qcc;

import com.wuji.common.config.AbstractFeignRequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

@Slf4j
public class QccFeignRequestInterceptor extends AbstractFeignRequestInterceptor {

    @Value("${qcc.server:http://jisuqygsxx.market.alicloudapi.com}")
    private String qccServer;

    @Value("${qcc.key}")
    private String key;

    @Value("${qcc.secret}")
    private String secret;

    @Value("${qcc.code}")
    private String code;



    @Override
    protected void doApply(RequestTemplate template) {
        // String time = String.valueOf(new Date().getTime() / 1000);
        // String token = Md5Utils.md5(key + time + secret);
        // template.header("Token", token.toUpperCase());
        // template.header("Timespan", time);
        template.header(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        template.header("Authorization", "APPCODE " + code);
    }

    @Override
    protected String serverName() {
        return "QCC";
    }

    @Override
    protected String server() {
        return qccServer;
    }
}
