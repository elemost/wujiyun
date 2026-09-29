package com.wuji.service.model.info;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormAggregateTableLookUp {
    private String applicationId;

    private MongodbSearchFilter filter;

    private List<String> formIds;



    private List<FormAggregateTableValField> valFields;

    private List<FormAggregateTableRelation> relations;

    private List<FormAggregateTableField> fieldXs;

    private List<FormAggregateTableField> fieldYs = new ArrayList<>();

    private List<FormAggregateTableField> joinedFields;
}
