package com.wuji.open.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WorkflowPendingListRequest {
    @NotNull(message = "page参数不能为空")
    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @NotNull(message = "pageSize参数不能为空")
    @ApiModelProperty("返回条数")
    private Integer pageSize = 100;

    private String applicationId;
}
