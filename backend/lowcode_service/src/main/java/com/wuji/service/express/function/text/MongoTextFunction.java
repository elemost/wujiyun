package com.wuji.service.express.function.text;

import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoTextFunction extends BaseMongoFunction {
    public MongoTextFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 1) {
            throw new BizException(name + "公式错误");
        }
        List<Object> collect = Arrays.stream(list).map(MongoSearchUtils::qlExpressValue).collect(Collectors.toList());
        return new Document("$cond", Arrays.asList(
                new Document("$or", Arrays.asList(new Document("$eq", Arrays.asList("$number_field", null)))), "",
                new Document("$toString", collect.get(0))));
    }
}
