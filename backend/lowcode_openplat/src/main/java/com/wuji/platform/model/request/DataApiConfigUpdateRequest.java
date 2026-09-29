package com.wuji.platform.model.request;

import lombok.Data;

@Data
public class DataApiConfigUpdateRequest {
    private String id;

    private String configName;

    private String config;

    private Integer applicationId;
}
