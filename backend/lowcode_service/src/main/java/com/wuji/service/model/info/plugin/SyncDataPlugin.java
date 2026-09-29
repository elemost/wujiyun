package com.wuji.service.model.info.plugin;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SyncDataPlugin extends PluginCommonConfig {

    private String url;

    private String appKey;
}
