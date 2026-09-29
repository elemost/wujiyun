package com.wuji.admin.model.request;

import lombok.Data;

@Data
public class CompanyRequest {
    private String companyName;

    private Short companyType;

    private String pullConfig;

    private String secretId;

    private String dataSource;

    private String channelType;

    private String suiteId;

    private String corpId;
}
