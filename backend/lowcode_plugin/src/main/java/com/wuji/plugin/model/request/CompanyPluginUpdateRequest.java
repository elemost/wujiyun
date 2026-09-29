package com.wuji.plugin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompanyPluginUpdateRequest {
    private String id;

    private String pluginConfig;

    private String pluginType;

    private String pluginName;

    @ApiModelProperty("插件参数")
    private String pluginParam;
}
