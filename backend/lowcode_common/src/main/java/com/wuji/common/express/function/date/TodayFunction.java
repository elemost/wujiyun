package com.wuji.common.express.function.date;

import com.ql.util.express.Operator;
import com.wuji.common.utils.TimeUtils;

import java.util.Date;
import java.util.Objects;

public class TodayFunction extends Operator {
    public TodayFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {
        return Objects.requireNonNull(
                        TimeUtils.convertDate(TimeUtils.formatDateTime(new Date(), TimeUtils.TIME_DATE), TimeUtils.TIME_DATE))
                .getTime();
    }
}
