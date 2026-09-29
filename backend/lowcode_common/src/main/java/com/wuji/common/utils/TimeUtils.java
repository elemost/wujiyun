package com.wuji.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


/**
 * 常用时间转换工具类
 *
 * @author hzm
 * @since 2024-03-04
 */
@Slf4j
public class TimeUtils {

    public static final String TIME_DATE = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_SIMPLE = "yyyyMMddHHmmss";

    public static final String DATE_SIMPLE = "yyMMdd";

    public static final String DATE_SIMPLE_8 = "yyyyMMdd";

    public static final String MONTH_SIMPLE = "yyyyMM";

    public static final String YEAR_SIMPLE = "yyyy";
    public static final String TIME_FORMAT_CN = "yyyy年MM月dd日 HH:mm:ss";
    public static final String GMT_8 = "GMT+8";
    private static final Pattern CHECK_TIME = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String SECOND = "second";
    public static final String MINUTE = "minute";

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static Long getCurrentTimeMills() {
        return System.currentTimeMillis();
    }

    public static String getUTCTime() {
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(cal.getTime());
    }

    /**
     * 获取当前的UTM时间
     *
     * @return
     */
    public static String getUTMDateStr() {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(cd.getTime());

        return date;
    }

    /**
     * 获取两个时间差
     *
     * @param beginTime
     * @param endTime
     * @param type      day hour minute second 给几个返回几个
     * @return
     * @throws ParseException
     */
    public static String getTimeDifference(String beginTime, String endTime, String... type) {
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat dfs = new SimpleDateFormat(TIME_DATE);
        if (beginTime == null) {
            return stringBuilder.toString();
        }
        try {
            Date end;
            if (StringUtils.isBlank(endTime)) {
                end = new Date();
            } else {
                end = dfs.parse(endTime);
            }
            Date begin = dfs.parse(beginTime);

            long diff = end.getTime() - begin.getTime();

            for (String s : type) {
                if (DAY.equals(s)) {
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    stringBuilder.append(diffDays).append("天");
                }
                if (HOUR.equals(s)) {
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    stringBuilder.append(diffHours).append("时");
                }
                if (MINUTE.equals(s)) {
                    long diffMinutes = diff / (60 * 1000) % 60;
                    stringBuilder.append(diffMinutes).append("分");
                }
                if (SECOND.equals(s)) {
                    long diffSeconds = diff / 1000 % 60;
                    stringBuilder.append(diffSeconds + "秒");
                }
            }
        } catch (Exception e) {

        }

        return stringBuilder.toString();
    }

    public static Long getTimeDifference(Date beginTime, Date endTime) {
        Date begin = getDataZero(beginTime, 0);
        Date end = getDataZero(endTime, 0);
        long diffInMillis = Math.abs(end.getTime() - begin.getTime());
        return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) + 1;
    }


    public static Date getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    public static String getStartTime() {
        Calendar cal = getCurrentUtcTime();
        cal.add(Calendar.MILLISECOND, -(61 * 60 * 1000));
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(cal.getTime());
    }

    private static Calendar getCurrentUtcTime() {
        // 1、获取当前时间
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        final int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        final int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        return cal;
    }

    public static String getEndTime() {
        Calendar cal = getCurrentUtcTime();
        cal.add(Calendar.MILLISECOND, -(1 * 60 * 1000));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(cal.getTime());
    }

    /**
     * 转换时间，将string类型转换成字符串的"yyyy-MM-dd HH:mm:ss"
     */
    public static Date convertDate(String dataTimeStr) {
        if (StringUtils.isEmpty(dataTimeStr)) {
            return null;
        }
        return convertDate(dataTimeStr, TIME_FORMAT);
    }

    public static Date convertDate(String dataTimeStr, String dataFormat) {
        if (StringUtils.isEmpty(dataTimeStr)) {
            return null;
        }
        Date dataTime = null;
        try {
            // dataFormat = "yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat df = new SimpleDateFormat(dataFormat);
            dataTime = df.parse(dataTimeStr);
        } catch (Exception e) {
            log.error("时间转换出现异常", e);
            return null;
        }

        return dataTime;
    }

    public static String formatDateTime(Date currDate) {
        return formatDateTime(currDate, TIME_FORMAT);
    }

    public static boolean checkTime(String value) {
        return CHECK_TIME.matcher(value).matches();
    }

    /**
     * 转换时间，将date类型按时间格式转换成字符串
     */
    public static String formatDateTime(Date currDate, String format) {
        if (currDate == null) {
            return null;
        }

        SimpleDateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(currDate);
        } catch (Exception e) {
            log.error("时间转换出现异常", e);
            return null;
        }
    }

    /**
     * 根据输入的当前时间、天数和输出时间格式，输出当前时间加天数
     */
    public static String addDay(Date currentDate, int days, String format) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, days);
        currentDate = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String format1 = sdf.format(currentDate);

        return format1;
    }

    /**
     * 根据输入的当前时间、天数和输出时间格式，输出当前时间加天数
     */
    public static String dateAddDaysTranStr(Date currentDate, int days, String format) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, days);
        currentDate = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String date = sdf.format(currentDate);

        return date;
    }

    /**
     * 根据选择的查询时间类型和查询结束时间，输出查询开始时间
     */
    public static List<Long> transZeroByType(String type, Date date) {
        List<Long> dateList = new ArrayList<>();
        if (null == date) {
            return null;
        }
        Long startTime = 0L;
        Long endTime = 0L;
        switch (type) {
            case "today":
                startTime = getDataZero(date, 0).getTime();
                endTime = getDataZero(date, 1).getTime();
                break;
            case "yesterday":
                startTime = getDataZero(date, -1).getTime();
                endTime = getDataZero(date, 0).getTime();
                break;
            case "tomorrow":
                startTime = getDataZero(date, 1).getTime();
                endTime = getDataZero(date, 2).getTime();
                break;
            case "thirty_day":
                startTime = getDataZero(date, -30).getTime();
                endTime = getDataZero(date, 0).getTime();
                break;
            case "seven_day":
                startTime = getDataZero(date, -6).getTime();
                endTime = getDataZero(date, 0).getTime();
                break;
            case "this_week":
                startTime = getWeekZero(date, 0, Calendar.SUNDAY).getTime();
                endTime = getWeekZero(date, 1, Calendar.SUNDAY).getTime();
                break;
            case "last_week":
                startTime = getWeekZero(date, -1, Calendar.SUNDAY).getTime();
                endTime = getWeekZero(date, 0, Calendar.SUNDAY).getTime();
                break;
            case "next_week":
                startTime = getWeekZero(date, 1, Calendar.SUNDAY).getTime();
                endTime = getWeekZero(date, 2, Calendar.SUNDAY).getTime();
                break;
            case "this_month":
                startTime = getMonthZero(date, 0, 1).getTime();
                endTime = getMonthZero(date, 1, 1).getTime();
                break;
            case "last_month":
                startTime = getMonthZero(date, -1, 1).getTime();
                endTime = getMonthZero(date, 0, 1).getTime();
                break;
            case "next_month":
                startTime = getMonthZero(date, 1, 1).getTime();
                endTime = getMonthZero(date, 2, 1).getTime();
                break;
            case "this_quarter":
                startTime = getQuarterZero(date, 0).getTime();
                endTime = getQuarterZero(date, 1).getTime();
                break;
            case "last_quarter":
                startTime = getQuarterZero(date, -1).getTime();
                endTime = getQuarterZero(date, 0).getTime();
                break;
            case "next_quarter":
                startTime = getQuarterZero(date, 1).getTime();
                endTime = getQuarterZero(date, 2).getTime();
                break;
            case "this_year":
                startTime = getYearZero(date, 0, Calendar.JANUARY, 1).getTime();
                endTime = getYearZero(date, 1, Calendar.JANUARY, 1).getTime();
                break;
            case "last_year":
                startTime = getYearZero(date, -1, Calendar.JANUARY, 1).getTime();
                endTime = getYearZero(date, 0, Calendar.JANUARY, 1).getTime();
                break;
            case "next_year":
                startTime = getYearZero(date, 1, Calendar.JANUARY, 1).getTime();
                endTime = getYearZero(date, 2, Calendar.JANUARY, 1).getTime();
                break;
            default:
                break;
        }
        dateList.add(startTime);
        dateList.add(endTime);
        return dateList;
    }

    public static Date getTransTime(String unit, Integer value) {
        switch (unit) {
            case "d":
                return getDataZero(new Date(), value);
            case "w":
                return getDataZero(new Date(), value * 7);
            case "m":
                return getMonthZero(new Date(), value, null);
            case "y":
                return getYearZero(new Date(), value, null, null);
        }
        return new Date();
    }

    public static Date getDataZero(Date date, int amount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getMinuteZero(Date date, int amount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, amount);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getHourZero(Date date, int amount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, amount);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDateByOffset(Date date, int offset) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, offset);
        return calendar.getTime();
    }

    public static Date getDataEnd(Date date, int amount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getWeekZero(Date date, int weekAmount, Integer firstDayOfWeek) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"), Locale.CHINA);
        calendar.setTime(date);
        if (firstDayOfWeek != null) {
            calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        }
        calendar.add(Calendar.WEEK_OF_YEAR, weekAmount);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getMonthZero(Date date, int monthAmount, Integer day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.add(Calendar.MONTH, monthAmount);
        if (day != null) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
        }
        // 设置时间为0点
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }


    public static Date getQuarterZero(Date date, int quarterAmount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        // 获取当前月份
        int month = calendar.get(Calendar.MONTH);


        int firstMonthOfPreviousQuarter = ((month / 3) * 3 + 3 * quarterAmount) % 12;
        int year = ((month / 3) * 3 + 3 * quarterAmount) / 12;
        if (firstMonthOfPreviousQuarter < 0) {
            firstMonthOfPreviousQuarter += 12;
            // 如果上季度跨年，则减去一年
            calendar.add(Calendar.YEAR, year - 1);
        } else {
            calendar.add(Calendar.YEAR, year);
        }

        calendar.set(Calendar.MONTH, firstMonthOfPreviousQuarter);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 设置时间为0点
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getYearZero(Date date, int yearAmount, Integer month, Integer day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        // 设置为去年的第一天
        if (month != null) {
            calendar.set(Calendar.MONTH, month);
        }
        if (day != null) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
        }
        // 设置时间为0点
        calendar.add(Calendar.YEAR, yearAmount);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 从当前时间获取时间段
     *
     * @param type
     * @param endTime
     * @return
     */
    public static Date transDateByTypeFromCurrentTime(String type, Date endTime) {

        if (null == endTime) {
            return null;
        }

        Date startTime = null;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(endTime);
        // 调用csp接口使用
        switch (type) {
            case "hour":
                calendar.add(Calendar.HOUR, -23);
                startTime = calendar.getTime();
                break;
            case "day":
                calendar.add(Calendar.DATE, -29);
                startTime = calendar.getTime();
                break;
            case "month":
                calendar.add(Calendar.MONTH, -11);
                startTime = calendar.getTime();
                break;
            case "year":
                calendar.add(Calendar.YEAR, -9);
                startTime = calendar.getTime();
                break;
            default:
                break;
        }

        return startTime;
    }

    public static Date getDate(Integer year, Integer month, Integer day, Integer week) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        if (week == null) {
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DATE, day);
        } else {
            calendar.set(Calendar.WEEK_OF_YEAR, week);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取两个时间戳之间（包含起始和结束当天）每一天的 0 点时间戳（毫秒）
     * 使用 java.util.Calendar
     *
     * @param timestamp1 第一个时间戳（毫秒）
     * @param timestamp2 第二个时间戳（毫秒）
     * @return 包含每一天 0 点时间戳的列表（毫秒）
     */
    public static List<Long> getMidnightTimestamps(long timestamp1, long timestamp2) {
        // 创建 Calendar 实例并设置时区
        Calendar cal = Calendar.getInstance();

        // 确定起始和结束时间戳
        long startTs = Math.min(timestamp1, timestamp2);
        long endTs = Math.max(timestamp1, timestamp2);

        // 设置 Calendar 到起始时间戳
        cal.setTimeInMillis(startTs);
        // 清除时间部分，只保留日期（设置为当天 00:00:00）
        clearTime(cal);

        List<Long> midnights = new ArrayList<>();

        // 遍历从起始日期到结束日期（包含）
        while (cal.getTimeInMillis() <= endTs) {
            // 将当前 Calendar 的时间（已经是 00:00:00）添加到列表
            midnights.add(cal.getTimeInMillis());

            // 增加一天
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return midnights;
    }

    /**
     * 辅助方法：清除 Calendar 的时间部分（时、分、秒、毫秒），设置为 00:00:00
     *
     * @param cal Calendar 实例
     */
    private static void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public static Date dateAddDayWithLastSecondAndUnit(Date dt, int day, String unit) {
        if ("month".equals(unit)) {
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.MONTH, day);
            //获取到rightNow当天的最后一秒日期
            rightNow.set(Calendar.HOUR_OF_DAY, 23);
            rightNow.set(Calendar.MINUTE, 59);
            rightNow.set(Calendar.SECOND, 59);
            return rightNow.getTime();
        } else if ("quarter".equals(unit)) {
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.MONTH, day * 3);
            //获取到rightNow当天的最后一秒日期
            rightNow.set(Calendar.HOUR_OF_DAY, 23);
            rightNow.set(Calendar.MINUTE, 59);
            rightNow.set(Calendar.SECOND, 59);
            return rightNow.getTime();
        } else if ("year".equals(unit)) {
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.YEAR, day);
            //获取到rightNow当天的最后一秒日期
            rightNow.set(Calendar.HOUR_OF_DAY, 23);
            rightNow.set(Calendar.MINUTE, 59);
            rightNow.set(Calendar.SECOND, 59);
            return rightNow.getTime();
        } else {
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.DAY_OF_YEAR, day);
            //获取到rightNow当天的最后一秒日期
            rightNow.set(Calendar.HOUR_OF_DAY, 23);
            rightNow.set(Calendar.MINUTE, 59);
            rightNow.set(Calendar.SECOND, 59);
            return rightNow.getTime();
        }
    }

    public static Date getDateByDaytime(String daytime) {
        try {
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            Date time = sdfTime.parse(daytime);
            // 2. 拿到今天的日期，并设置时分
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
            calendar.set(Calendar.MINUTE, time.getMinutes());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // 最终 Date 对象
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getDateByMinute(String daytime) {
        try {
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            Date time = sdfTime.parse(daytime);
            // 2. 拿到今天的日期，并设置时分
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
            calendar.set(Calendar.MINUTE, time.getMinutes());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // 最终 Date 对象
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }
}
