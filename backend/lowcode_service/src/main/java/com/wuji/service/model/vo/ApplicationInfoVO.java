package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplicationInfoVO {
    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("key")
    private String infoKey;

    @ApiModelProperty("value")
    private String infoValue;
}
