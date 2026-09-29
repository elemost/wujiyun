package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SinFunction extends Operator {

    public SinFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        if (lists.length != 1) {
            return 0;
        }

        double hudu = Double.parseDouble(lists[0].toString());

        return BigDecimal.valueOf(Math.sin(hudu)).setScale(1, RoundingMode.HALF_UP);
    }
}
