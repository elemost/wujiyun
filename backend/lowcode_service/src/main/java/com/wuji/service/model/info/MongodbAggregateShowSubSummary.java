package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class MongodbAggregateShowSubSummary {
    private List<String> subSummaryFields;

    private Boolean showSubSummary = Boolean.FALSE;
}
