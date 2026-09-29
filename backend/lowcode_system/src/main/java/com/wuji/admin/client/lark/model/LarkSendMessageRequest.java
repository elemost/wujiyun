package com.wuji.admin.client.lark.model;

import lombok.Data;

@Data
public class LarkSendMessageRequest {
    private String receive_id;

    private String msg_type;

    private String content;

    private String uuid;
}
