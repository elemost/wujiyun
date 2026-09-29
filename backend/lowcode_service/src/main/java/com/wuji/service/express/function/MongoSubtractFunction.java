package com.wuji.service.express.function;

import com.ql.util.express.Operator;
import com.wuji.common.exception.BizException;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoSubtractFunction extends Operator {

    public MongoSubtractFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 2) {
            throw new BizException(name + "公式错误");
        }
        List<Object> objectList = new ArrayList<>();
        for (Object o : list) {
            objectList.add(MongoSearchUtils.qlExpressValue(o));
        }
        return new Document("$subtract", objectList);
    }
}
