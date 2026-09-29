package com.wuji.service.express.function.logic;

import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoOrFunction extends BaseMongoFunction {

    public MongoOrFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) {
        if (lists.length == 0) {
            throw new BizException(name + "公式错误");
        }
        List<Object> collect = Arrays.stream(lists).map(MongoSearchUtils::qlExpressValue).collect(Collectors.toList());
        return new Document("$cond",
                new Document("if", new Document("$or", collect)).append("then", true).append("else", false));
    }


}
