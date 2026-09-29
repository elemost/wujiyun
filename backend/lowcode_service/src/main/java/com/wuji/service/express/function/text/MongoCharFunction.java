package com.wuji.service.express.function.text;

import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

public class MongoCharFunction extends BaseMongoFunction {
    public MongoCharFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 1) {
            throw new BizException(name + "公式错误");
        }
        return new Document("$convert",
                new Document("input", MongoSearchUtils.qlExpressValue(list[0])).append("to", "string")
                        .append("onError", "").append("onNull", ""));
    }
}
