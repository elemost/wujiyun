package com.wuji.service.express.function.date;

import com.ql.util.express.Operator;
import com.wuji.common.exception.BizException;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

public class MongoDateFunction extends Operator {
    public MongoDateFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 1) {
            throw new BizException(name + "公式错误");
        }
        Object object = MongoSearchUtils.qlExpressValue(list[0]);
        Document append = new Document().append("format", "%Y-%m-%d %H:%M:%S").append("timezone", "Asia/Shanghai")
                .append("date", new Document("$toDate", object));
        return new Document("$dateToString", append);
    }
}
