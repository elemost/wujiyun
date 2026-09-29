package com.wuji.service.model.info.plugin;

import com.wuji.common.model.request.ApiRequest;
import com.wuji.plugin.model.info.HttpIdentityAuth;
import com.wuji.plugin.model.info.PluginReturnMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class HttpPlugin extends PluginCommonConfig {
    private ApiRequest apiRequest;

    private List<HttpPluginRequestMapping> requestMappingList;

    private PluginReturnMapping returnMapping;

    private HttpIdentityAuth identityAuth;
}
