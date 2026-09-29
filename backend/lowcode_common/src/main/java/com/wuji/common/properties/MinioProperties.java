package com.wuji.common.properties;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "minio")
@Data
@Component
public class MinioProperties {
    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    public String getEndpoint() {
        if (StringUtils.isEmpty(endpoint)) {
            return String.format("http://%s:9000", "wujiminio");
        } else {
            return endpoint;
        }
    }
}
