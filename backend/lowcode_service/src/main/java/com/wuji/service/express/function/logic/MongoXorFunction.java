package com.wuji.service.express.function.logic;

import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import org.bson.Document;

public class MongoXorFunction extends BaseMongoFunction {

    public MongoXorFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) {
        if (lists.length != 3) {
            throw new BizException(name + "公式错误");
        }
        return new Document("$cond", new Document("$or", lists).append("then", lists[1]).append("else", lists[2]));
    }


}
