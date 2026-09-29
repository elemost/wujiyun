package com.wuji.service.express.function.text;

import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoReptFunction extends BaseMongoFunction {
    public MongoReptFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 2) {
            throw new BizException(name + "公式错误");
        }
        List<Object> collect = Arrays.stream(list).map(MongoSearchUtils::qlExpressValue).collect(Collectors.toList());
        return new Document("$reduce",
                new Document("input", new Document("$range", Arrays.asList(0, collect.get(1)))).append("initialValue",
                        "").append("in", new Document("$concat",
                        Arrays.asList("$$value", MongoFunctionUtils.ifNullString(collect.get(0))))));
    }
}
