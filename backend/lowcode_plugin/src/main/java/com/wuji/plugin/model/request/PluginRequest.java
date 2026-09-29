package com.wuji.plugin.model.request;

import lombok.Data;

@Data
public class PluginRequest {

    private String id;

    private String pluginConfig;

    private String pluginType;

    private String pluginParam;

    private String pluginReturn;

    private String pluginName;

    private String functionType;
}
