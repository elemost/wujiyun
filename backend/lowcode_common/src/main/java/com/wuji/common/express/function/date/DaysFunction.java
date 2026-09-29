package com.wuji.common.express.function.date;

import com.ql.util.express.Operator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;


public class DaysFunction extends Operator {

    public DaysFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        // 边界判断
        if (lists.length == 0 || lists.length > 3) {
            return null;
        }

        // 获取参数值
        LocalDate date1 = localDate(Long.parseLong(lists[0].toString()));
        LocalDate date2 = localDate(Long.parseLong(lists[1].toString()));

        return ChronoUnit.DAYS.between(date1, date2);
    }

    private static LocalDate localDate(long timestamp) {
        // 将时间戳转换为Instant对象
        Instant instant = Instant.ofEpochMilli(timestamp);
        // 将Instant对象转换为LocalDate对象
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();

    }
}
