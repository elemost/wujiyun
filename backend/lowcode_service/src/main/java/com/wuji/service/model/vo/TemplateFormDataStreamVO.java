package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateFormDataStreamVO {
    private String id;

    @ApiModelProperty("名字")
    private String name;

    @ApiModelProperty("配置类型")
    private String configType;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("画布配置")
    private String canvasConfig;

    @ApiModelProperty("状态")
    private String state;

    private Integer version;

    private Boolean enable;
}
