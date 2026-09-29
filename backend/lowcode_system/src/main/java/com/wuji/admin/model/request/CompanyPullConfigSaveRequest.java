package com.wuji.admin.model.request;

import lombok.Data;

@Data
public class CompanyPullConfigSaveRequest {
    private Long companyId;

    private String pullConfig;

    private String configType;

    private String sourceAppId;

    private String appId;
}
