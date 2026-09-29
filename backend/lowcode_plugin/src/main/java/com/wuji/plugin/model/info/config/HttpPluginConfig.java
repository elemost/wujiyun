package com.wuji.plugin.model.info.config;

import com.wuji.common.model.request.ApiRequest;
import com.wuji.plugin.model.info.HttpIdentityAuth;
import com.wuji.plugin.model.info.PluginReturnMapping;
import lombok.Data;

@Data
public class HttpPluginConfig {
    private PluginReturnMapping returnMapping;

    private ApiRequest apiRequest;

    private HttpIdentityAuth identityAuth;
}
