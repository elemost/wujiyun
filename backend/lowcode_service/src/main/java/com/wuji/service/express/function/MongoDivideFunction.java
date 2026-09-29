package com.wuji.service.express.function;

import com.ql.util.express.Operator;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDivideFunction extends Operator {

    public MongoDivideFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        List<Object> objectList = new ArrayList<>();
        for (Object o : list) {
            objectList.add(MongoSearchUtils.qlExpressValue(o));
        }
        if (StringUtils.isNumeric(list[1].toString())) {
            Document divide = new Document("$divide", objectList);
            return Document.parse(divide.toJson());
        } else {
            Document document = MongoFunctionUtils.ifNull(objectList.get(1));
            List<Object> eqList = new ArrayList<>();
            eqList.add(document);
            eqList.add(0);
            Document divide = new Document("$cond",
                    new Document("if", new Document("$eq", eqList)).append("then", new Document("$literal", null))
                            .append("else", new Document("$divide", objectList)));
            return Document.parse(divide.toJson());
        }
    }
}
