package com.wuji.admin.client.wecom.model;

import lombok.Data;

@Data
public class SuiteTokenRequest {
    private String suite_id;

    private String suite_secret;

    private String suite_ticket;
}
