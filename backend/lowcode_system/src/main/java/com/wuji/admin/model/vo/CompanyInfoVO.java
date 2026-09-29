package com.wuji.admin.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompanyInfoVO {

    private Long companyId;

    @ApiModelProperty("配置key")
    private String configKey;

    @ApiModelProperty("配置的值")
    private String configValue;
}
