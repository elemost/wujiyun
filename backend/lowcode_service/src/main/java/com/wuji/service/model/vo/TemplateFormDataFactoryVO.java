package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateFormDataFactoryVO {
    private String id;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("数据工厂类型")
    private String factoryType;

    @ApiModelProperty("数据工厂配置")
    private String factoryConfig;

    @ApiModelProperty("对应表名")
    private String tableName;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private String factoryName;

    private String syncConfig;

    private Integer version;

    private Boolean syncForm;
}
