package com.wuji.service.express.function;

import com.ql.util.express.Operator;
import com.wuji.common.exception.BizException;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

public class MongoSumFunction extends Operator {
    public MongoSumFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 1) {
            throw new BizException(name + "公式错误");
        }
        Object object = MongoSearchUtils.qlExpressValue(list[0]);
        return new Document("$sum", object);
    }
}
