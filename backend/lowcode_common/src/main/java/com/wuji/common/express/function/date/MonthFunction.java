package com.wuji.common.express.function.date;

import com.ql.util.express.Operator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

public class MonthFunction extends Operator {

    public MonthFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        // 边界判断
        if (lists.length != 1) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(lists[0].toString())),
                ZoneId.systemDefault());
        Month month = dateTime.getMonth();
        return month.getValue();
    }
}
