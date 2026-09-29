package com.wuji.service.model.request;

import lombok.Data;

@Data
public class ApplicationCopyRequest {
    private String applicationId;

    private Boolean needData;

    private String applicationName;
}
