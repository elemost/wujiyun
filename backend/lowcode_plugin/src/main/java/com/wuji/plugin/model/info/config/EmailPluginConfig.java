package com.wuji.plugin.model.info.config;

import lombok.Data;

@Data
public class EmailPluginConfig {

    private String pluginId;

    private String title;

    private String content;

    private String sendUser;

    private String userName;

    private String password;

    private String mailHost;

    private String mailPort;

}
