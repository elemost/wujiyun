package com.wuji.plugin.model.info.auth;

import com.wuji.plugin.model.info.HttpIdentityAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WeComApp extends HttpIdentityAuth {

    private String corpId;

    private String clientSecret;

}
