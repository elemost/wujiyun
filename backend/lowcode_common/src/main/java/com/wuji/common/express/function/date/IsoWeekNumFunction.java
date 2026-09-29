package com.wuji.common.express.function.date;


import com.ql.util.express.Operator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Locale;


public class IsoWeekNumFunction extends Operator {

    public IsoWeekNumFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) {

        if (lists.length != 1) {
            return null;
        }

        LocalDate date = localDate(Long.parseLong(lists[0].toString()));
        // 获取ISO周数
        return date.get(WeekFields.of(Locale.US).weekOfWeekBasedYear());
    }


    private static LocalDate localDate(long timestamp) {
        // 将时间戳转换为Instant对象
        Instant instant = Instant.ofEpochMilli(timestamp);
        // 将Instant对象转换为LocalDate对象
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();

    }

}
