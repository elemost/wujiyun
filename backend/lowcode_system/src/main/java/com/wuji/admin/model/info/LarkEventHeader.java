package com.wuji.admin.model.info;

import lombok.Data;

@Data
public class LarkEventHeader {
    private String event_id;

    private String token;

    private String event_type;

    private Long create_time;

    private String app_id;

    private String tenant_key;
}
