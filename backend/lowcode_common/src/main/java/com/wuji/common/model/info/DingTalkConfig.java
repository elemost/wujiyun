package com.wuji.common.model.info;

import lombok.Data;

@Data
public class DingTalkConfig {
    private String aesKey;

    private String aesToken;

    private String clientId;

    private String clientSecret;

    private String agentId;

    private String robotCode;

    private String corpId;
}
