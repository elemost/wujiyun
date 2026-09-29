package com.wuji.service.model.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplicationDomain {
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

    @ApiModelProperty("状态")
    private String state;
}
