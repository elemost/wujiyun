package com.wuji.common.model.info;

import lombok.Data;

@Data
public class WeComConfig {
    private String corpId;

    private Integer clientId;

    private String clientSecret;

    private String aesKey;

    private String aesToken;

    private String permanentCode;
}
