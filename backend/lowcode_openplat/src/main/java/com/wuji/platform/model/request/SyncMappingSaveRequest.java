package com.wuji.platform.model.request;

import lombok.Data;

@Data
public class SyncMappingSaveRequest {

    private String applicationId;

    private String formId;

    private String mappingConfig;
}
