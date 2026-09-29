package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormPublicPublishVO {
    private String applicationId;

    private String formId;

    @ApiModelProperty("发布类型")
    private String publishType;

    @ApiModelProperty("发布配置")
    private String config;

    private String id;

    private Short state;

    private String accessToken;
}
