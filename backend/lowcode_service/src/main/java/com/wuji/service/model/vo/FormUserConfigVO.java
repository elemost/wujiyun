package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormUserConfigVO {
    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("配置类型")
    private String configType;

    private Long userId;

    @ApiModelProperty("配置")
    private String config;

    private String formId;
}
