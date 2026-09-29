package com.wuji.common.utils;

import com.wuji.common.enums.RepeatTriggerEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 时间间隔判断工具：判断两个时间是否相隔 指定周期的整倍数
 * 支持：1天、1周、2周、1月、1年
 */
public class TimeIntervalUtils {
    public static boolean isTimeCycleMatch(Date startTime, Date endTime, RepeatTriggerEnum cycle) {
        LocalDateTime start = convertToLocalDateTime(startTime);
        LocalDateTime end = convertToLocalDateTime(endTime);
        // 一次性任务，直接返回 true（无需间隔）
        if (cycle == RepeatTriggerEnum.ONCE) {
            return isSameDayZero(start, end);
        }
        switch (cycle) {
            case DAILY:
                return isDayMultiple(start, end);
            case WEEKLY:
                return isWeekMultiple(start, end);
            case TWO_WEEKLY:
                return isTwoWeeksMultiple(start, end);
            case MONTHLY:
                return isMonthMultiple(start, end);
            case YEARLY:
                return isYearMultiple(start, end);
            default:
                return false;
        }
    }

    // ===================== ONCE 核心：判断两个时间的 当天零点 是否一致 =====================
    private static boolean isSameDayZero(LocalDateTime s, LocalDateTime e) {
        LocalDate date1 = s.toLocalDate();
        LocalDate date2 = e.toLocalDate();
        return date1.equals(date2);
    }

    // ====================== 使用 LocalDateTime（含时分秒） ======================

    /**
     * 判断两个时间是否相隔 【1天的整倍数】
     * 规则：时间差 % 1天 == 0
     */
    public static boolean isDayMultiple(LocalDateTime start, LocalDateTime end) {
        long days = ChronoUnit.DAYS.between(start, end);
        return true;
    }

    /**
     * 判断两个时间是否相隔 【1周的整倍数】（7天）
     */
    public static boolean isWeekMultiple(LocalDateTime start, LocalDateTime end) {
        long days = ChronoUnit.DAYS.between(start, end);
        return days % 7 == 0;
    }

    /**
     * 判断两个时间是否相隔 【2周的整倍数】（14天）
     */
    public static boolean isTwoWeeksMultiple(LocalDateTime start, LocalDateTime end) {
        long days = ChronoUnit.DAYS.between(start, end);
        return days % 14 == 0;
    }

    /**
     * 判断两个时间是否相隔 【1月的整倍数】
     * 规则：自然月整倍数（如 2025-01-01 和 2025-02-01、2025-03-01 都符合）
     */
    public static boolean isMonthMultiple(LocalDateTime start, LocalDateTime end) {
        long months = ChronoUnit.MONTHS.between(start, end);
        // 校验：月份差是整月，且 日+时分秒 完全一致（避免 2025-01-31 与 2025-02-28 这种非整月）
        LocalDateTime plusMonths = start.plusMonths(months);
        return plusMonths.equals(end);
    }

    /**
     * 判断两个时间是否相隔 【1年的整倍数】
     * 规则：自然年整倍数
     */
    public static boolean isYearMultiple(LocalDateTime start, LocalDateTime end) {
        long years = ChronoUnit.YEARS.between(start, end);
        // 校验：年份差是整年，且 月日时分秒 完全一致
        LocalDateTime plusYears = start.plusYears(years);
        return plusYears.equals(end);
    }

    // ====================== Date 转 LocalDateTime ======================
    private static LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}