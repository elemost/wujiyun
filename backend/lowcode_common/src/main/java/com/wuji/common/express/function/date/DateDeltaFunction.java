package com.wuji.common.express.function.date;


import com.ql.util.express.Operator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateDeltaFunction extends Operator {


    public DateDeltaFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        LocalDate localDateRst = null;
        if (lists.length != 2) {
            return null;
        }

        LocalDate localDate = localDate(Long.parseLong(lists[0].toString()));

        int deltadays = Integer.parseInt(lists[1].toString());

        int days = 0;
        if (deltadays < 0) {
            days = -deltadays;
            localDateRst = localDate.minusDays(days);
        } else {
            localDateRst = localDate.plusDays(deltadays);
        }
        // 格式化为字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateRst.format(formatter);
    }

    private static LocalDate localDate(long timestamp) {
        // 将时间戳转换为Instant对象
        Instant instant = Instant.ofEpochMilli(timestamp);
        // 将Instant对象转换为LocalDate对象
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();

    }
}
