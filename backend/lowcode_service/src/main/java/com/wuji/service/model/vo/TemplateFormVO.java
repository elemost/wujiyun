package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateFormVO {
    private String id;

    @ApiModelProperty("来源id")
    private String sourceId;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("对应表名")
    private String tableName;

    @ApiModelProperty("版本")
    private Integer version;

    @ApiModelProperty("表单类型")
    private String formType;

    private String formConfig;

    private String applicationId;
}
