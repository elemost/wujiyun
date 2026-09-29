package com.wuji.service.model.mongo;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

public class ProjectAggregation implements AggregationOperation {

    private final Document project;

    public ProjectAggregation(Document project) {
        this.project = project;
    }

    @Override
    public Document toDocument(AggregationOperationContext context) {
        project.append("_id", 0);
        return new Document("$project", project);

    }
}
