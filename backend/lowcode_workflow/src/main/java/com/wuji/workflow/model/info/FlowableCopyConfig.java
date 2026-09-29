package com.wuji.workflow.model.info;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FlowableCopyConfig {
    @ApiModelProperty("抄送对象类型")
    private String businessType;

    @ApiModelProperty("抄送对象id")
    private String businessId;

    private String businessName;
}
