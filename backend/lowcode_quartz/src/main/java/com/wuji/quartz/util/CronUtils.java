package com.wuji.quartz.util;

import com.wuji.common.enums.RepeatTriggerEnum;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * cron表达式工具类
 *
 * @author ruoyi
 */
public class CronUtils {
    /**
     * 返回一个布尔值代表一个给定的Cron表达式的有效性
     *
     * @param cronExpression Cron表达式
     * @return boolean 表达式是否有效
     */
    public static boolean isValid(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * 返回一个字符串值,表示该消息无效Cron表达式给出有效性
     *
     * @param cronExpression Cron表达式
     * @return String 无效时返回表达式错误描述,如果有效返回null
     */
    public static String getInvalidMessage(String cronExpression) {
        try {
            new CronExpression(cronExpression);
            return null;
        } catch (ParseException pe) {
            return pe.getMessage();
        }
    }

    /**
     * 返回下一个执行时间根据给定的Cron表达式
     *
     * @param cronExpression Cron表达式
     * @return Date 下次Cron表达式执行时间
     */
    public static Date getNextExecution(String cronExpression) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(new Date(System.currentTimeMillis()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static String trans(RepeatTriggerEnum repeatTrigger, Date startTime, String cron) {
        if (repeatTrigger == RepeatTriggerEnum.ONCE) {
            return getOnceCron(startTime);
        } else if (repeatTrigger == RepeatTriggerEnum.DAILY) {
            return getDailyCron(startTime);
        } else if (repeatTrigger == RepeatTriggerEnum.MONTHLY) {
            return getMonthlyCron(startTime);
        } else if (repeatTrigger == RepeatTriggerEnum.WEEKLY) {
            return getWeeklyCron(startTime);
        } else if (repeatTrigger == RepeatTriggerEnum.YEARLY) {
            return getYearlyCron(startTime);
        } else if (repeatTrigger == RepeatTriggerEnum.TWO_WEEKLY) {
            return getTwoWeeklyCron(startTime);
        } else if (repeatTrigger == RepeatTriggerEnum.CUSTOM) {
            return getCustomCron(startTime, cron);
        }
        return null;
    }


    private static String getOnceCron(Date startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        return String.format("%d %d %d %d %d ?", calendar.get(Calendar.SECOND), calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1); // Calendar.MONTH 从 0 开始
    }

    private static String getDailyCron(Date startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        return String.format("0 %d %d * * ?", calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY));
    }

    private static String getWeeklyCron(Date startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1=周日, 2=周一, ..., 7=周六
        // 转换为 Cron 的周格式 (1=周一, 7=周日)
        dayOfWeek = (dayOfWeek == 1) ? 7 : dayOfWeek - 1;
        return String.format("0 %d %d ? * %d", calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY),
                dayOfWeek);
    }

    private static String getTwoWeeklyCron(Date startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1=周日, 2=周一, ..., 7=周六
        // 转换为 Cron 的周格式 (1=周一, 7=周日)
        dayOfWeek = (dayOfWeek == 1) ? 7 : dayOfWeek - 1;
        return String.format("0 %d %d ? * %d */2", calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY),
                dayOfWeek);
    }

    private static String getMonthlyCron(Date startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        return String.format("0 %d %d %d * ?", calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static String getYearlyCron(Date startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        return String.format("0 %d %d %d %d ?", calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1); // Calendar.MONTH 从 0 开始
    }

    private static String getCustomCron(Date startTime, String cron) {
        List<String> collect = Arrays.stream(cron.split(",")).collect(Collectors.toList());
        String firstConfig = collect.get(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        if (firstConfig.contains("W")) {
            firstConfig = firstConfig.substring(0, firstConfig.length() - 1);
            collect.remove(0);
            return String.format("0 %d %d ? * %s */%s", calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.HOUR_OF_DAY), StringUtils.join(collect, ","), firstConfig);
        } else if (firstConfig.contains("M")) {
            firstConfig = firstConfig.substring(0, firstConfig.length() - 1);
            collect.remove(0);
            return String.format("0 %d %d %s */%s ?", calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY),
                    StringUtils.join(collect, ","), firstConfig);
        } else if (firstConfig.contains("D")) {

        }
        return null;
    }

}
