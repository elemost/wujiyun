package com.wuji.common.express.function.date;


import com.ql.util.express.Operator;
import com.wuji.common.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateFunction extends Operator {

    public DateFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        if (lists.length == 0 || lists.length > 6) {
            return null;
        }

        Object rst = null;

        if (lists.length == 1) {
            rst = format(Long.parseLong(lists[0].toString()));
        } else if (lists.length == 3) {
            rst = format(Integer.parseInt(lists[0].toString()), Integer.parseInt(lists[1].toString()),
                    Integer.parseInt(lists[2].toString()));
        } else if (lists.length == 6) {
            rst = format(Integer.parseInt(lists[0].toString()), Integer.parseInt(lists[1].toString()),
                    Integer.parseInt(lists[2].toString()), Integer.parseInt(lists[3].toString()),
                    Integer.parseInt(lists[4].toString()), Integer.parseInt(lists[5].toString()));
        }
        return rst;
    }

    private static String format(int year, int month, int day) {

        // 创建一个LocalDate对象
        LocalDate localDate = LocalDate.of(year, month, day);
        // 将LocalDate对象转换为ZonedDateTime对象
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        // 将ZonedDateTime对象转换为Instant对象
        Instant instant = zonedDateTime.toInstant();
        // 将Instant对象转换为Date对象
        Date date = Date.from(instant);
        // 创建SimpleDateFormat对象，指定目标字符串的格式
        SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.TIME_FORMAT);
        // 格式化Date对象为目标字符串
        return sdf.format(date);

    }

    private static String format(int year, int month, int day, int hour, int minute, int second) {
        // 创建一个LocalDate对象
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        // 创建SimpleDateFormat对象，指定目标字符串的格式
        SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.TIME_FORMAT);
        // 格式化Date对象为目标字符串
        return sdf.format(date);

    }

    private static String format(long timestamp) {
        // 将时间戳转换为Instant对象
        Instant instant = Instant.ofEpochMilli(timestamp);
        // 将Instant对象转换为LocalDate对象

        // 将Instant对象转换为Date对象
        Date date = Date.from(instant);
        // 创建SimpleDateFormat对象，指定目标字符串的格式
        SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.TIME_FORMAT);
        // 格式化Date对象为目标字符串
        return sdf.format(date);
    }
}
