package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormQuoteSaveRequest {

    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("类型")
    private String businessType;

    private String applicationId;

    private String businessId;


    // DASH
    private String type;

    private List<FormQuoteSaveInfoRequest> formQuoteSaveInfoList;
}
