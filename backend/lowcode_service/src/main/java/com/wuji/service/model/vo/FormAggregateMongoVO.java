package com.wuji.service.model.vo;

import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.List;

@Data
public class FormAggregateMongoVO {
    private String tableName;

    private List<AggregationOperation> aggregationList;
}