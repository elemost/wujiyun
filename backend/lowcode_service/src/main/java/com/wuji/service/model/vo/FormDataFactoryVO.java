package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormDataFactoryVO {
    private String id;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("数据工厂类型")
    private String factoryType;

    @ApiModelProperty("数据工厂配置")
    private String factoryConfig;

    @ApiModelProperty("对应表名")
    private String tableName;

    private String factoryName;

    private String syncConfig;

    private String status;

    private Boolean syncConfigError;

    private Boolean syncForm;
}
