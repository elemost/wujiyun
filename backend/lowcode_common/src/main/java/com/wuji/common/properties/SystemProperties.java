package com.wuji.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lowcode")
@Data
public class SystemProperties {
    private String limitCount;

    private String env;

    private String uploadType;
}
