package com.wuji.service.express.function.math;

import com.google.common.collect.Lists;
import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

public class MongoFloorFunction extends BaseMongoFunction {
    public MongoFloorFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 2) {
            throw new BizException(name + "公式错误");
        }
        Object value = MongoSearchUtils.qlExpressValue(list[0]);
        Object factor = MongoSearchUtils.qlExpressValue(list[1]);

        return new Document("$multiply",
                Lists.newArrayList(new Document("$floor", new Document("$divide", Lists.newArrayList(value, factor))),
                        factor));
    }
}
