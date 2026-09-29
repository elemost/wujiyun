package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateFormPublicPublishVO {

    private String applicationId;

    private String formId;

    @ApiModelProperty("发布类型")
    private String publishType;

    @ApiModelProperty("发布配置")
    private String config;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    @ApiModelProperty("状态")
    private Short state;

    private String accessToken;
}
