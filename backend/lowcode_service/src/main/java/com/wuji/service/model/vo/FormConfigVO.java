package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormConfigVO {
    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("配置类型")
    private String configType;

    @ApiModelProperty("表单配置")
    private String formConfig;
}
