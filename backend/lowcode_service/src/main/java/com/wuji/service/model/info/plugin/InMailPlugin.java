package com.wuji.service.model.info.plugin;

import com.wuji.admin.model.info.UserScope;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class InMailPlugin extends PluginCommonConfig {
    private String content;

    private List<UserScope> scopes;

    private String messageType;

}
