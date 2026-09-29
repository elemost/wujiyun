package com.wuji.systemapi.client.user.model;

import lombok.Data;

import java.util.Date;

@Data
public class CompanySyncRequest {
    private String companyUuid;

    private String companyName;

    private Date createTime;

    private String clientId;

    private String equityId;

    private Date startTime;

    private Date endTime;

    private Integer userCount;

    private String remoteAddr;
}
