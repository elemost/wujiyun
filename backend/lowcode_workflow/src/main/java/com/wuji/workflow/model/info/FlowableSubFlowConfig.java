package com.wuji.workflow.model.info;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FlowableSubFlowConfig {
    private String subFormId;

    @ApiModelProperty("父数据传递给子数据")
    private List<FlowableDataTrans> parentFlowableDataTrans = new ArrayList<>();

    @ApiModelProperty("子数据传递给父数据")
    private List<FlowableDataTrans> childFlowableDataTrans = new ArrayList<>();

}
