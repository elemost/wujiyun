package com.wuji.service.express.function.math;

import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;

public class MongoSqrtFunction extends BaseMongoFunction {

    public MongoSqrtFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 1) {
            throw new BizException(name + "公式错误");
        }
        Object object = MongoSearchUtils.qlExpressValue(list[0]);

        return new Document("$cond", new Document().append("if", new Document("$and",
                        Arrays.asList(new Document("$ne", Arrays.asList(object, null)),
                                new Document("$gte", Arrays.asList(object, 0))))).append("then", new Document("$sqrt", object))
                .append("else", null));
    }
}
