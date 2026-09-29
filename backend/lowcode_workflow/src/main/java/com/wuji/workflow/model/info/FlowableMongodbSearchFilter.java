package com.wuji.workflow.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FlowableMongodbSearchFilter {
    private List<FlowableMongodbSearchCondition> conditionList;

    private String rel;
}
