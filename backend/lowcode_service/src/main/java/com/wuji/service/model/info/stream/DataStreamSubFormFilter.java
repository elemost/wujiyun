package com.wuji.service.model.info.stream;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataStreamSubFormFilter {

    @ApiModelProperty("赋值的子表单")
    private String subForm;

    @ApiModelProperty("节点id")
    private Long nodeId;

    @ApiModelProperty("过滤条件")
    private DataStreamConditionRel condition;

    @ApiModelProperty("引用子表单")
    private String quoteSubForm;

    @ApiModelProperty("引用的子表单  当子表单为查询多条时")
    private String quoteTableName;

    public String getQuoteKey() {
        return getNodeId() + "_" + quoteSubForm + "_" + quoteTableName;
    }

    public Long getNodeId() {
        if (nodeId == null) {
            return null;
        }
        if (nodeId == 2) {
            return 1L;
        } else {
            return nodeId;
        }
    }
}
