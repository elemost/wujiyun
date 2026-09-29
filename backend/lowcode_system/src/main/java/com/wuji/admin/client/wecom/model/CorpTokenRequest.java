package com.wuji.admin.client.wecom.model;

import lombok.Data;

@Data
public class CorpTokenRequest {
    private String auth_corpid;

    private String permanent_code;
}
