package com.wuji.service.express.function.date;

import com.ql.util.express.Operator;
import org.bson.Document;


public class MongoTodayFunction extends Operator {

    public MongoTodayFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return new Document("$toLong", new Document("$dateTrunc",
                new Document("date", "$$NOW").append("unit", "day").append("timezone", "Asia/Shanghai")));
    }
}
