package com.wuji.service.express.function.text;

import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoConcatenateFunction extends BaseMongoFunction {
    public MongoConcatenateFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        List<Object> collect = Arrays.stream(list).map(MongoSearchUtils::qlExpressValue).collect(Collectors.toList());
        return new Document("$concat", collect);
    }
}
