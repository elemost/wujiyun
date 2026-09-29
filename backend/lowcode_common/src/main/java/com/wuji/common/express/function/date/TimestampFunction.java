package com.wuji.common.express.function.date;

import com.ql.util.express.Operator;
import com.wuji.common.utils.TimeUtils;

import java.util.Objects;

public class TimestampFunction extends Operator {

    public TimestampFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {
        // 边界判断
        if (lists.length != 1) {
            return null;
        }
        return Objects.requireNonNull(TimeUtils.convertDate(lists[0].toString(), TimeUtils.TIME_FORMAT))
                .getTime();
    }
}
