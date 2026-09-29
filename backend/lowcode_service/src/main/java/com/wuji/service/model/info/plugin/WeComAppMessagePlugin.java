package com.wuji.service.model.info.plugin;

import com.wuji.admin.model.info.UserScope;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class WeComAppMessagePlugin extends PluginCommonConfig {
    private String content;

    private String messageType;

    private List<UserScope> scopes;
}
