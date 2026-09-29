package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormUserConfigSaveRequest {

    private String formId;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("配置类型")
    private String configType;

    @ApiModelProperty("配置")
    private String config;

    // WORKBENCH CONTROL
    private String source;
}
