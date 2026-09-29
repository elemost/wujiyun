package com.wuji.service.model.info;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormAggregateTable {
    private String applicationId;

    private String formId;

    private MongodbSearchFilter filter;

    private List<String> formIds;

    private Boolean onlyGroup;

    private List<FormAggregateTableValField> valFields;

    private List<FormAggregateTableRelation> relations;

    private List<FormAggregateTableField> fieldXs;

    private List<FormAggregateTableField> fieldYs = new ArrayList<>();

    private List<FormAggregateTableField> joinedFields;
}
