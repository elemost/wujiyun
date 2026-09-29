package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormPublicPublishSaveRequest {
    private String applicationId;

    private String formId;

    @ApiModelProperty("发布类型")
    private String publishType;

    @ApiModelProperty("发布配置")
    private String config;

    private String id;

    private Short state;
}
