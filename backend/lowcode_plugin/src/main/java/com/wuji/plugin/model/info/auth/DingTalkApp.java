package com.wuji.plugin.model.info.auth;

import com.wuji.plugin.model.info.HttpIdentityAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DingTalkApp extends HttpIdentityAuth {
    private String clientId;

    private String clientSecret;
}
