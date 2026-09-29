package com.wuji.admin.client.wecom.model;

import lombok.Data;

import java.util.Map;

@Data
public class AppMessageRequest {
    private String touser;

    private String msgtype;

    private Integer agentid;

    private Map<String, String> text;

    private Map<String, String> markdown;

    private Integer safe = 0;

    private Integer enable_id_trans = 0;

    private Integer enable_duplicate_check = 0;
}
