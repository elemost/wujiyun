package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormDataFactoryUpdateRequest {
    private String id;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("数据工厂配置")
    private String factoryConfig;

    @ApiModelProperty("对应表名")
    private String tableName;

    private String status;

    private String factoryName;
}
