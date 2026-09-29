package com.wuji.service.express.function.date;

import com.ql.util.express.Operator;
import org.bson.Document;

public class MongoNowFunction extends Operator {

    public MongoNowFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return new Document("$toLong", "$$NOW");
    }
}
