package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateFormExtraFunctionRelationVO {
    @ApiModelProperty("功能id")
    private String functionId;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("业务类型")
    private String businessType;
}
