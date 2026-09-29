package com.wuji.common.properties;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cos")
@Data
public class CosProperties {
    private String endPoint;

    private String bucketName;

    private String accessKeyId;

    private String accessKeySecret;

    private String region;

    private String redirectCos;

    public String getEndPoint() {
        if (StringUtils.isNotEmpty(redirectCos)) {
            return redirectCos;
        }
        return endPoint;
    }
}
