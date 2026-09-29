package com.wuji.plugin.model.info.config;

import com.wuji.admin.model.info.UserScope;
import lombok.Data;

import java.util.List;

@Data
public class InMailPluginConfig {
    private String content;

    private List<UserScope> scopes;

    private String messageType;

    private String source;
}
