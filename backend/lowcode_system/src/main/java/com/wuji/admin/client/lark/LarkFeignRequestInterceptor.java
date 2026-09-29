package com.wuji.admin.client.lark;

import com.wuji.admin.cache.LarkLoginCache;
import com.wuji.common.config.AbstractFeignRequestInterceptor;
import com.wuji.common.utils.UserUtils;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import java.util.Collection;

@Slf4j
public class LarkFeignRequestInterceptor extends AbstractFeignRequestInterceptor {

    @Value("${lark.server:https://open.feishu.cn}")
    private String plateServer;

    @Override
    protected void doApply(RequestTemplate template) {
        String url = template.url();
        Collection<String> contentType = template.headers().get(HTTP.CONTENT_TYPE);
        if (!url.contains("/open-apis/auth/v3/app_access_token/internal") &&
                !url.contains("/open-apis/authen/v1/user_info")) {
            final String token = LarkLoginCache.getAccessToken(UserUtils.getUser().getCompanyId());
            if (token != null) {
                template.header("Authorization", token);
            }
        }
        template.header(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    }

    @Override
    protected String serverName() {
        return "PLATE";
    }

    @Override
    protected String server() {
        return plateServer;
    }
}
