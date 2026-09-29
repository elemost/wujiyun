package com.wuji.service.utils;

import com.wuji.common.utils.TimeUtils;

import java.util.Date;

public class SerialNumberUtils {
    public static String getDateTimeKey(String cycle) {
        String dateTimeKey = "";
        if ("DAY".equals(cycle)) {
            dateTimeKey = TimeUtils.formatDateTime(new Date(), TimeUtils.DATE_SIMPLE_8);
        } else if ("YEAR".equals(cycle)) {
            dateTimeKey = TimeUtils.formatDateTime(new Date(), TimeUtils.YEAR_SIMPLE);
        } else if ("MONTH".equals(cycle)) {
            dateTimeKey = TimeUtils.formatDateTime(new Date(), TimeUtils.MONTH_SIMPLE);
        } else if ("NEVER".equals(cycle)) {
            dateTimeKey = "NEVER";
        }
        return dateTimeKey;
    }
}
