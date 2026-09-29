package com.wuji.service.model.info;

import lombok.Data;

@Data
public class MongodbAggregateShowSummary {
    private Boolean shouldShowSummaryCol = Boolean.FALSE;

    private Boolean shouldShowSummaryRow = Boolean.FALSE;

    private String sumColPosition;

    private String sumRowPosition;
}
