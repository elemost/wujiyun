package com.wuji.workflow.model.info;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FlowableFormFieldConfig {

    private String type;

    private String name;

    private String label;

    private Object value;

    @ApiModelProperty("是否可见")
    private Boolean visibleFlag;

    @ApiModelProperty("是否可编辑")
    private Boolean editableFlag;

    private Boolean briefingFlag = Boolean.FALSE;

    @ApiModelProperty("是否必填")
    private Boolean nonEditableFlag;

    private Boolean requiredFlag;

    private List<FlowableFormFieldConfig> children;

}
