package com.wuji.plugin.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompanyPluginVO {
    private String id;

    private String pluginConfig;

    private String pluginType;

    private String pluginName;

    @ApiModelProperty("插件参数")
    private String pluginParam;

    private String pluginReturn;

    private String functionType;
}
