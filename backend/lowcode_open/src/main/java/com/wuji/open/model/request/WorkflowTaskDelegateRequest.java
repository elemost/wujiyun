package com.wuji.open.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WorkflowTaskDelegateRequest {
    private String processInstanceId;

    private String taskId;

    private String comment;

    @ApiModelProperty("委派用户")
    private Long userId;
}
