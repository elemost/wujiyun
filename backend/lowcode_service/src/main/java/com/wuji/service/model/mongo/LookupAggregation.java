package com.wuji.service.model.mongo;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import java.util.List;
import java.util.stream.Collectors;

public class LookupAggregation implements AggregationOperation {

    private final String fromCollection;
    private final List<Document> pipeline;
    private final String as;

    private final Document let;


    public LookupAggregation(String fromCollection, List<Document> pipeline, Document let, String as) {
        this.fromCollection = fromCollection;
        this.pipeline = pipeline;
        this.let = let;
        this.as = as;
    }

    /**
     * 核心修复：使用AggregationOperationContext处理管道和变量的解析
     * @param context 上下文，用于解析字段、变量、集合名等
     * @return 标准化的$lookup文档
     */
    @Override
    public Document toDocument(AggregationOperationContext context) {
        // 1. 处理let中的变量：通过上下文解析字段路径（关键！）
        Document resolvedLet = new Document();
        if (let != null && !let.isEmpty()) {
            for (String key : let.keySet()) {
                Object value = let.get(key);
                // 解析字段路径（如将"instValue.field_ml7tzoa1"转为正确的MongoDB路径）
                resolvedLet.put(key, context.getMappedObject(new Document("$expr", value)).get("$expr"));
            }
        }

        // 2. 处理内部pipeline：每个阶段都要通过上下文解析（关键！）
        List<Document> resolvedPipeline = pipeline.stream()
                .map(context::getMappedObject) // 解析每个管道阶段的字段/变量
                .collect(Collectors.toList());

        // 3. 构建标准化的$lookup文档

        return new Document("$lookup",
                new Document("from", fromCollection)
                        .append("let", resolvedLet)
                        .append("pipeline", resolvedPipeline)
                        .append("as", as)
        );
    }
}
