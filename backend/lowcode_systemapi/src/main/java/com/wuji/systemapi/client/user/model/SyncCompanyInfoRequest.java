package com.wuji.systemapi.client.user.model;

import lombok.Data;

@Data
public class SyncCompanyInfoRequest {
    private String syncData;

    private String companyUuid;

    private String uuid;
}
