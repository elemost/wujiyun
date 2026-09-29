package com.wuji.service.client.model;

import com.wuji.common.model.domain.UserDomain;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PluginUseDirectRequest {
    private String pluginType;

    private Object requestJson;

    private UserDomain userDomain;
}
