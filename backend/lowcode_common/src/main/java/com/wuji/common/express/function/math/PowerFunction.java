package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

import java.math.BigDecimal;

public class PowerFunction extends Operator {

    public PowerFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length == 0 || list.length > 2) {
            return null;
        }

        BigDecimal number = new BigDecimal(list[0].toString());
        int power = Integer.parseInt(list[1].toString());
        return calculatePower(number, power);
    }

    public static BigDecimal calculatePower(BigDecimal number, int power) {
        return number.pow(power);
    }
}
