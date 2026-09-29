package com.wuji.service.model.mongo;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

public class AddFieldAggregation implements AggregationOperation {



    @Override
    public Document toDocument(AggregationOperationContext context) {
        return null;
    }
}
