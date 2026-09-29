package com.wuji.service.express.function.date;

import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

public class MongoMinuteFunction extends BaseMongoFunction {

    public MongoMinuteFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length != 1) {
            throw new BizException(name + "公式错误");
        }
        return new Document("$minute",
                new Document().append("date", new Document("$toDate", MongoSearchUtils.qlExpressValue(list[0])))
                        .append("timezone", "+08:00"));
    }
}
