package com.wuji.admin.model.request;

import lombok.Data;

@Data
public class CompanySaveRequest {
    private String companyUuid;

    private String logo;

    private String companyName;
}
