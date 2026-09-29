package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompanyInfoSaveRequest {
    @ApiModelProperty("配置key")
    private String configKey;

    @ApiModelProperty("配置的值")
    private String configValue;

    private Long companyId;
}
