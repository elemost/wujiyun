package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormInfoUpdateRequest {
    private String id;

    private String applicationId;

    private String formId;

    private String infoConfig;

    @ApiModelProperty("是否为默认详情页")
    private Boolean defaultConfig;

    @ApiModelProperty("详情页名称")
    private String infoName;

    private String otherConfig;
}
