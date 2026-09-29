package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class MongodbWidget {
    private List<MongodbSearchField> fieldyList;

    private List<MongodbSearchField> fieldxList;

    private List<MongodbSearchField> metricList;

    private MongodbSearchFilter filter;

    private List<MongodbSearchField> fields;

    private String type;

    private String permission;

    private MongodbAggregateShowSummary showSummary = new MongodbAggregateShowSummary();

    private MongodbAggregateShowSubSummary showSubSummary;

    private List<MongoSort> defaultSorts;

    private MongodbWidgetTop showTopNum;

    private List<MongodbAggregateFormula> formulas;
}
