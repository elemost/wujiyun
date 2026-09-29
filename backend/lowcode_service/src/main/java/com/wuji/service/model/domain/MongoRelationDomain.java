package com.wuji.service.model.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

@Data
public class MongoRelationDomain {
    private AggregationOperation leftAggregationOperation;

    private AggregationOperation rightAggregationOperation;

    private String type;

}
