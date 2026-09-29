package com.wuji.service.express.function.math;

import com.wuji.service.express.function.BaseMongoFunction;
import org.bson.Document;

public class MongoRandFunction extends BaseMongoFunction {

    public MongoRandFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        return new Document("$rand", new Document());
    }
}

