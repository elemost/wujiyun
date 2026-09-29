package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormQuoteSaveInfoRequest {
    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("类型")
    private String businessType;

    @ApiModelProperty("对应类型id")
    private String businessId;

    @ApiModelProperty("引用表单")
    private String quoteFormId;

    @ApiModelProperty("引用字段")
    private String quoteField;

    @ApiModelProperty("引用字段类型")
    private String quoteFieldType;

    @ApiModelProperty("业务字段类型")
    private String businessFieldType;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("是否关联聚合")
    private Boolean aggregate;

    private String quoteType;
}
