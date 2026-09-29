package com.wuji.admin.model.vo;

import lombok.Data;

@Data
public class ClientFunctionVO {
    private String clientId;

    private String permissionKey;

    private String permissionName;

    private Integer limitCount;

    private String equityId;
}
