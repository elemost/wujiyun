package com.wuji.common.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
@ConfigurationProperties(prefix = "wecom")
@Data
public class WeComProperties {

    private List<WeComConfig> configs;

    private String suiteId;

    public void setConfigs(List<WeComConfig> configs) {
        this.configs = configs;
    }

    public WeComConfig getBySuiteId(String suiteId) {
        return this.getConfigs().stream().collect(Collectors.toMap(WeComConfig::getSuiteId, c -> c)).get(suiteId);
    }

    @Data
    public static class WeComConfig {
        private String token;

        private String aseKey;

        private String corpId;

        private String suiteId;

        private String suiteSecret;
    }
}
