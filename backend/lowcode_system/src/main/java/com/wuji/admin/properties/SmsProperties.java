package com.wuji.admin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sms")
@Data
public class SmsProperties {
    private String accessKeyId;

    private String accessKeySecret;

    private String sign;

    private String templateId;

    private String smsSdkAppid;
}
