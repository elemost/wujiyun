package com.wuji.common.express.function.date;


import com.ql.util.express.Operator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class DayFunction extends Operator {

    public DayFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        if (lists.length != 1) {
            return null;
        }

        Object rst = null;

        rst = day(Long.parseLong(lists[0].toString()));

        return rst;
    }

    private static int day(long timestamp) {
        // 将时间戳转换为Instant对象
        Instant instant = Instant.ofEpochMilli(timestamp);
        // 将Instant对象转换为LocalDate对象
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        return localDate.getDayOfMonth();

    }
}
