package com.wuji.common.model.info;

import lombok.Data;

@Data
public class LarkConfig {
    private String aesKey;

    private String aesToken;

    private String clientId;

    private String clientSecret;
}
