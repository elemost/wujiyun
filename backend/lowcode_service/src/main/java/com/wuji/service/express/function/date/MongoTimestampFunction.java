package com.wuji.service.express.function.date;

import com.ql.util.express.Operator;
import com.wuji.common.exception.BizException;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

public class MongoTimestampFunction extends Operator {

    public MongoTimestampFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length != 1) {
            throw new BizException(name + "公式错误");
        }
        return new Document("$toLong", MongoSearchUtils.qlExpressValue(list[0]));
    }
}
