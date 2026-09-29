package com.wuji.service.express.function.math;

import com.google.common.collect.Lists;
import com.wuji.common.exception.BizException;
import com.wuji.service.express.function.BaseMongoFunction;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;

public class MongoLogFunction extends BaseMongoFunction {

    public MongoLogFunction(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 2) {
            throw new BizException(name + "公式错误");
        }
        Object object = MongoSearchUtils.qlExpressValue(list[0]);
        Document document = new Document("$divide", Arrays.asList(new Document("$ln", object),
                new Document("$ln", MongoSearchUtils.qlExpressValue(list[1]))));
        Document gtCondition = new Document("$gt", Lists.newArrayList(object, 0));
        return new Document("$cond", new Document("if", gtCondition).append("then", document).append("else", null));
    }
}
