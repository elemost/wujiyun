package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormPublishPublishRequest {
    private String id;
    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("来源id")
    private String sourceId;

    @ApiModelProperty("配置")
    private String config;

    private Integer version;

    private String tableName;

    private String formType;

    private String applicationId;

    private String formConfig;

}
