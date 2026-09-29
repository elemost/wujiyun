package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormDataFactoryCreateRequest {
    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("数据工厂类型")
    private String factoryType;

    @ApiModelProperty("数据工厂配置")
    private String factoryConfig;

    private String status;

}
