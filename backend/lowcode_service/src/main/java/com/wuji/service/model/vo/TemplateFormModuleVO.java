package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateFormModuleVO {
    private String id;

    @ApiModelProperty("对应的表单id")
    private String formId;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("组件类型")
    private String moduleType;

    @ApiModelProperty("应用id")
    private String applicationId;
}
