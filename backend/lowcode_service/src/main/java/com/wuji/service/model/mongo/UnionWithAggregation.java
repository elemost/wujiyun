package com.wuji.service.model.mongo;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import java.util.List;

public class UnionWithAggregation implements AggregationOperation {

    private final List<Document> pipelineList;

    private final String coll;

    public UnionWithAggregation(String coll, List<Document> pipelineList) {
        this.pipelineList = pipelineList;
        this.coll = coll;
    }

    @Override
    public Document toDocument(AggregationOperationContext context) {
        Document unionWith = new Document("$unionWith", new Document("coll", coll).append("pipeline", pipelineList));
        return unionWith;
    }
}
