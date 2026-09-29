package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FloorFunction extends Operator {

    public FloorFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length != 2) {
            return 0;
        }
        BigDecimal number = new BigDecimal(list[0].toString());
        BigDecimal significance = new BigDecimal(list[1].toString());
        return floor(number, significance);
    }

    public static BigDecimal floor(BigDecimal number, BigDecimal significance) {
        BigDecimal divided = number.divide(significance, 0, RoundingMode.DOWN);
        return divided.multiply(significance);
    }
}
