package com.wuji.service.express.function.text;

import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoRightFunction extends BaseMongoFunction {
    public MongoRightFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 2) {
            throw new BizException(name + "公式错误");
        }
        List<Object> collect = Arrays.stream(list).map(MongoSearchUtils::qlExpressValue).collect(Collectors.toList());
        return new Document("$let", new Document().append("vars",
                new Document().append("str", MongoFunctionUtils.ifNullString(collect.get(0))).append("targetLen", collect.get(1))).append("in",
                new Document("$cond", new Document().append("if",
                                new Document("$gte", Arrays.asList(new Document("$strLenCP", "$$str"), "$$targetLen")))
                        .append("then", new Document("$substrCP", Arrays.asList("$$str", new Document("$subtract",
                                Arrays.asList(new Document("$strLenCP", "$$str"), "$$targetLen")), "$$targetLen")))
                        .append("else", "$$str"))));
    }
}
