package com.wuji.service.express.function.date;

import com.ql.util.express.Operator;
import com.wuji.common.exception.BizException;
import com.wuji.service.utils.MongoSearchUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoDatedifFunction extends Operator {

    public MongoDatedifFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length != 3) {
            throw new BizException(name + "公式错误");
        }
        String unit = transUnit(list[2]);
        List<Object> collect = Arrays.stream(list).map(MongoSearchUtils::qlExpressValue).collect(Collectors.toList());
        return new Document("$dateDiff",
                new Document("startDate", new Document("$toDate", collect.get(0))).append("endDate",
                        new Document("$toDate", collect.get(1))).append("unit", unit));
    }

    private static String transUnit(Object unit) {
        switch (unit.toString()) {
            case "d":
                return "day";
            case "s":
                return "second";
            case "h":
                return "hour";
            case "m":
                return "minute";
            case "y":
                return "year";
            case "M":
                return "month";

        }
        return null;
    }
}
