package com.wuji.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ding")
@Data
public class DingTalkProperties {
    private String token;

    private String aseKey;

    private String corpId;

    private String suiteId;

    private String suiteSecret;
}
