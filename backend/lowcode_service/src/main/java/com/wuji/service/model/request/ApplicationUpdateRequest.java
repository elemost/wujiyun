package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplicationUpdateRequest {
    private String id;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("访问地址")
    private String visitUrl;

    private String icon;

    private String applicationType;
}
