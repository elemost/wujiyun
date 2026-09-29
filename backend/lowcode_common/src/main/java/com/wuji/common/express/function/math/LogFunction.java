package com.wuji.common.express.function.math;


import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.math.MathContext;

public class LogFunction extends Operator {

    public LogFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length == 0 || list.length >2) {
            return 0;
        }

        BigDecimal number = new BigDecimal(list[0].toString());
        BigDecimal rst = BigDecimal.ZERO;
        if (list.length == 1) {
            BigDecimal base = BigDecimal.TEN;
            rst = log(number,base);
        } else {
            BigDecimal base = new BigDecimal(list[1].toString());
            rst = log(number,base);
        }
        return rst;
    }

    public static BigDecimal log(BigDecimal number, BigDecimal base) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal logBase = BigDecimal.valueOf(Math.log(base.doubleValue()));

        MathContext mathContext = new MathContext(100); // 设置精度为100
        BigDecimal epsilon = BigDecimal.valueOf(1e-50); // 设置一个较小的误差范围

        while (number.compareTo(base) >= 0) {
            result = result.add(BigDecimal.ONE, mathContext);
            number = number.divide(base, mathContext);
        }

        BigDecimal fraction = BigDecimal.ZERO;
        BigDecimal power = BigDecimal.ONE;

        while (power.compareTo(epsilon) > 0) {
            power = power.divide(base, mathContext);

            if (number.compareTo(BigDecimal.ONE) >= 0) {
                number = number.divide(base, mathContext);
                fraction = fraction.add(power, mathContext);
            }
        }
        return result;
    }

}