package com.wuji.service.model.mongo;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

public class MapOperation implements AggregationOperation {

    private final String input;

    private final String as;

    private final Object in;

    public MapOperation(String input, String as, Object in) {
        this.in = in;
        this.input = input;
        this.as = as;
    }

    @Override
    public Document toDocument(AggregationOperationContext context) {
        Document document = new Document();
        document.append("input", "$" + input).append("as", as).append("in", in);
        return new Document().append("$map", document);
    }
}
