package com.wuji.platform.model.request;

import lombok.Data;

@Data
public class DataApiConfigCreateRequest {
    private String configName;

    private String config;

    private String applicationId;

    private String configType;
}
