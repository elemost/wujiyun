package com.wuji.systemapi.client.user;

import com.wuji.common.config.AbstractFeignRequestInterceptor;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.UserUtils;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class SystemFeignRequestInterceptor extends AbstractFeignRequestInterceptor {

    @Value("${lowcode.system}")
    private String plateServer;

    @Value("${lowcode.uuidSecret:5wjLesGbhMShB8ZX}")
    private String uuidSecret;

    @Override
    protected void doApply(RequestTemplate template) {
        String encryptUuid = AESUtils.encrypt(uuidSecret, UserUtils.getUser().getCompanyUuid());
        template.header("Company", encryptUuid);
    }

    @Override
    protected String serverName() {
        return "SYSTEM_USER";
    }

    @Override
    protected String server() {
        return plateServer;
    }
}
