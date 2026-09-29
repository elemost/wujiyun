package com.wuji.service.express.function.math;

import com.google.common.collect.Lists;
import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoCeilingFunction extends BaseMongoFunction {
    public MongoCeilingFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 2) {
            throw new BizException(name + "公式错误");
        }
        List<Object> collect = Arrays.stream(list).map(MongoSearchUtils::qlExpressValue).collect(Collectors.toList());
        return new Document("$multiply", Lists.newArrayList(new Document("$ceil", new Document("$divide", collect))));
    }
}
