package com.wuji.service.express.function.date;

import com.google.common.collect.Lists;
import com.ql.util.express.Operator;
import com.wuji.common.exception.BizException;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDatedeltaFunction extends Operator {

    public MongoDatedeltaFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length != 2) {
            throw new BizException(name + "公式错误");
        }
        List<Object> objectList = new ArrayList<>();
        Document document = new Document();
        objectList.add(MongoSearchUtils.qlExpressValue(list[0]));
        Document multiply =
                new Document("$multiply", Lists.newArrayList(MongoSearchUtils.qlExpressValue(list[1]), 86400000));
        Object object = MongoSearchUtils.qlExpressValue(list[0]);
        return document.append("$add", Lists.newArrayList(object, multiply));
    }
}
