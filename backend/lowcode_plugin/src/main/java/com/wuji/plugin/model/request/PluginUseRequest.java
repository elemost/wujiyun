package com.wuji.plugin.model.request;

import com.wuji.common.model.domain.UserDomain;
import com.wuji.plugin.model.info.PluginParamMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginUseRequest {
    private String pluginId;

    private List<PluginParamMapping> pluginMapping;

    private UserDomain userDomain;
}
