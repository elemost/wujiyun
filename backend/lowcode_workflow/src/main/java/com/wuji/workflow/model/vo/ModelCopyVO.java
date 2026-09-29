package com.wuji.workflow.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ModelCopyVO {
    @ApiModelProperty("流程模块id")
    private String modelId;

    @ApiModelProperty("绑定的流程id")
    private String processDefinitionId;
}
