package com.wuji.common.express.function.math;


import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IntFunction extends Operator {

    public IntFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length != 1) {
            return 0;
        }

        BigDecimal number = new BigDecimal(list[0].toString());

        return floorToInteger(number);
    }

    private BigDecimal floorToInteger(BigDecimal number) {
        return number.setScale(0, RoundingMode.FLOOR);
    }
}
