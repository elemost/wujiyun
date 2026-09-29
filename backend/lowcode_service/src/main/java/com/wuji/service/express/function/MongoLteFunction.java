package com.wuji.service.express.function;

import com.ql.util.express.Operator;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoLteFunction extends Operator {

    public MongoLteFunction(String name) {
        this.name = name;
    }
    @Override
    public Object executeInner(Object[] list) throws Exception {
        List<Object> objectList = new ArrayList<>();
        for (Object o : list) {
            objectList.add(MongoSearchUtils.qlExpressValue(o));
        }
        return new Document("$lte", objectList);
    }
}
