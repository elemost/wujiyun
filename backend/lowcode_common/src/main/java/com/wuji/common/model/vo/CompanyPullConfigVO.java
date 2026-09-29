package com.wuji.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompanyPullConfigVO {
    private String id;

    @ApiModelProperty("应用id")
    private String appId;

    @ApiModelProperty("拉取配置")
    private String pullConfig;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("配置类型")
    private String configType;

    private String sourceAppId;
}
