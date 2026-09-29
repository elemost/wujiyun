package com.wuji.service.express.function.text;

import com.google.common.collect.Lists;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;

public class MongoIsemptyFunction extends BaseMongoFunction {
    public MongoIsemptyFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        Object object = MongoSearchUtils.qlExpressValue(list[0]);
        return new Document("$or",
                Arrays.asList(new Document("$eq", Arrays.asList(new Document("$type", object), "missing")),
                        new Document("$and", Arrays.asList(new Document("$in",
                                        Arrays.asList(new Document("$type", object),
                                                Lists.newArrayList("double", "int", "long", "decimal"))),
                                new Document("$eq", Arrays.asList(MongoFunctionUtils.ifNullString(object), null)))),
                        new Document("$and", Arrays.asList(
                                new Document("$eq", Arrays.asList(new Document("$type", object), "string")),
                                new Document("$eq", Arrays.asList(MongoFunctionUtils.ifNullString(object), "")))),
                        new Document("$and", Arrays.asList(
                                new Document("$eq", Arrays.asList(new Document("$type", object), "array")),
                                new Document("$eq", Arrays.asList(new Document("$size", object), 0)))),
                        new Document("$and", Arrays.asList(
                                new Document("$eq", Arrays.asList(new Document("$type", object), "object")),
                                new Document("$eq", Arrays.asList(new Document("$bsonSize", object), 5))))));
    }
}
