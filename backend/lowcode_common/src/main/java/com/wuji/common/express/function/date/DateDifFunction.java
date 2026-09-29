package com.wuji.common.express.function.date;


import com.ql.util.express.Operator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class DateDifFunction extends Operator {

    public DateDifFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        if (lists.length == 0 || lists.length > 3 || lists.length == 1) {
            return null;
        }
        long startTimestamp = Long.parseLong(lists[0].toString());
        long endTimestamp = Long.parseLong(lists[1].toString());

        String unit = "";
        // 第三个值默认为d
        if (lists.length == 2) {
            unit = "d";
        } else {
            unit = lists[2].toString();
        }

        return handle(startTimestamp, endTimestamp, unit);
    }


    private Object handle(long startTimestamp, long endTimestamp, String unit) {

        // 异常判断  结束日期小于开始日期，则计算不出结果
        if (endTimestamp < startTimestamp) {
            return null;
        }
        Object rst;
        if ("y".equals(unit)) {
            rst = period(startTimestamp, endTimestamp, unit);
        } else if ("M".equals(unit)) {
            rst = period(startTimestamp, endTimestamp, unit);
        } else if ("d".equals(unit)) {
            rst = period(startTimestamp, endTimestamp, unit);
        } else if ("h".equals(unit)) {
            rst = duration(startTimestamp, endTimestamp, unit);
        } else if ("m".equals(unit)) {
            rst = duration(startTimestamp, endTimestamp, unit);
        } else if ("s".equals(unit)) {
            rst = duration(startTimestamp, endTimestamp, unit);
        } else {// unit枚举参数传错误  默认按照d处理
            rst = period(startTimestamp, endTimestamp, unit);
        }
        return rst;
    }

    private static LocalDate localDate(long timestamp) {
        // 将时间戳转换为Instant对象
        Instant instant = Instant.ofEpochMilli(timestamp);
        // 将Instant对象转换为LocalDate对象
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();

    }

    private static Object period(long startTimestamp, long endTimestamp, String unit) {

        Object rst = null;

        LocalDate startLocalDate = localDate(startTimestamp);
        LocalDate endLocalDate = localDate(endTimestamp);
        Period period = Period.between(startLocalDate, endLocalDate);

        if ("y".equals(unit)) {
            rst = ChronoUnit.YEARS.between(startLocalDate, endLocalDate);
        } else if ("M".equals(unit)) {
            rst = ChronoUnit.MONTHS.between(startLocalDate, endLocalDate);
        } else if ("d".equals(unit)) {
            rst = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
        } else {
            rst = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
        }
        return rst;
    }

    private static Object duration(long startTimestamp, long endTimestamp, String unit) {

        Object rst = null;

        LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTimestamp), ZoneId.systemDefault());
        LocalDateTime dateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), ZoneId.systemDefault());

        Duration duration = Duration.between(dateTime1, dateTime2);
        if ("h".equals(unit)) {
            rst = duration.toHours() % 24;
        } else if ("m".equals(unit)) {

            rst = duration.toMinutes() % 60;
        } else if ("s".equals(unit)) {

            rst = duration.getSeconds() % 60;
        }
        return rst;
    }


}
