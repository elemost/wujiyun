package com.wuji.service.model.info;

import com.wuji.common.utils.TimeUtils;
import com.wuji.service.enums.FormAggregateTimeTransTypeEnum;
import lombok.Data;

import java.util.Date;

@Data
public class FormAggregateDate {
    private Integer year;

    private Integer month = 1;

    private Integer day = 1;

    private Integer week;

    private Integer quarter;

    private Date transDate;

    private Date transNextDate;

    public Date getDate() {
        if (quarter == null) {
            return TimeUtils.getDate(getYear(), getMonth(), getDay(), getWeek());
        } else {
            return TimeUtils.getDate(getYear(), (getQuarter()) * 3, getDay(), getWeek());
        }
    }

    public Date getNextDate(String type) {
        if (FormAggregateTimeTransTypeEnum.DAY.name().equals(type)) {
            return TimeUtils.getDataZero(transDate, -1);
        } else if (FormAggregateTimeTransTypeEnum.MONTH.name().equals(type)) {
            return TimeUtils.getMonthZero(transDate, -1, null);
        } else if (FormAggregateTimeTransTypeEnum.YEAR.name().equals(type)) {
            return TimeUtils.getYearZero(transDate, -1, null, null);
        } else if (FormAggregateTimeTransTypeEnum.WEEK.name().equals(type)) {
            return TimeUtils.getWeekZero(transDate, -1, null);
        } else if (FormAggregateTimeTransTypeEnum.QUARTER.name().equals(type)) {
            return TimeUtils.getQuarterZero(transDate, -1);
        }
        return null;
    }

}
