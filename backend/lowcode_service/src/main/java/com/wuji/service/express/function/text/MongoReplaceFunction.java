package com.wuji.service.express.function.text;

import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoReplaceFunction extends BaseMongoFunction {
    public MongoReplaceFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 3) {
            throw new BizException(name + "公式错误");
        }
        List<Object> collect = Arrays.stream(list).map(MongoSearchUtils::qlExpressValue).collect(Collectors.toList());
        return new Document("$replaceAll", new Document().append("input", collect.get(0)).append("find", collect.get(1))
                .append("replacement", collect.get(2)));
    }
}