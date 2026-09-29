package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormPublishVO {
    private String id;

    private String config;

    @ApiModelProperty("版本")
    private Integer version;

    @ApiModelProperty("显示类型")
    private String showType;

    @ApiModelProperty("是否发布过表单")
    private Boolean published;

    private String formName;

    private String formType;
}
