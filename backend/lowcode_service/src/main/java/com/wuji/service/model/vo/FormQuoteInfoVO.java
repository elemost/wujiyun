package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormQuoteInfoVO {
    @ApiModelProperty("表单id")
    private String formId;

    private String formName;

    @ApiModelProperty("类型")
    private String businessType;

    @ApiModelProperty("对应类型id")
    private String businessId;

    @ApiModelProperty("引用表单")
    private String quoteFormId;

    @ApiModelProperty("引用表单")
    private String quoteFormName;

    @ApiModelProperty("引用字段")
    private String quoteField;

    private String quoteFieldType;

    private String businessFieldType;

    private String applicationId;

    private Boolean aggregate;

    private String quoteType;
}
