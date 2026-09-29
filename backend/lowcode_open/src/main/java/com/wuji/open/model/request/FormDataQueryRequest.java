package com.wuji.open.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormDataQueryRequest {

    private String formId;

    private String applicationId;

    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("返回条数")
    private Integer pageSize = 100;
}
