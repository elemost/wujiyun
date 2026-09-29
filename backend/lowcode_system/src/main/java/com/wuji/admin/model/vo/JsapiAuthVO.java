package com.wuji.admin.model.vo;

import lombok.Data;

@Data
public class JsapiAuthVO {
    private String appId;

    private Long timestamp;

    private String noncestr;

    private String signature;

    private String ticket;
}
