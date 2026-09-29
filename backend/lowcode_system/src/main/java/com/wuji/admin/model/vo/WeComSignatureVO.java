package com.wuji.admin.model.vo;

import lombok.Data;

@Data
public class WeComSignatureVO {
    private String corpId;

    private Integer agentId;

    private Long timestamp;

    private String noncestr;

    private String signature;
}
