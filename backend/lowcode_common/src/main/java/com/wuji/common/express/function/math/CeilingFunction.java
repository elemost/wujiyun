package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class CeilingFunction extends Operator {

    public CeilingFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {

        // 参数校验
        if (objects.length != 2) {
            return 0;
        }

        Object result = 0;
        // 考虑精度问题
        if (isPrecise) {
            BigDecimal number = new BigDecimal(objects[0].toString());
            BigDecimal multiple = new BigDecimal(objects[1].toString());
            result = precise(number, multiple);
        } else {
            // 根据objects数组中的第一个数，无论什么数据类型，一律按照double处理
            double n = Double.parseDouble(objects[0].toString());
            double s = Double.parseDouble(objects[1].toString());
            result = ceiling(n, s);
        }

        return result;
    }

    private static double ceiling(double n, double s) {
        if (n > 0.0D && s < 0.0D) {
            s = Math.abs(s);
        }
        return n > 0.0D && s < 0.0D ? Double.NaN : (n != 0.0D && s != 0.0D ? Math.ceil(n / s) * s : 0.0D);
    }

    private static BigDecimal precise(BigDecimal number, BigDecimal multiple) {
        BigDecimal zero = BigDecimal.ZERO;
        int comparisonResult = multiple.compareTo(zero);
        int flag = number.compareTo(zero);

        BigDecimal roundedNumber = BigDecimal.ZERO;
        BigDecimal multiply = number.divide(multiple, 0, RoundingMode.CEILING).multiply(multiple);
        if (flag >= 0 && comparisonResult >= 0) {
            roundedNumber = multiply;
        } else if (flag < 0 && comparisonResult >= 0) {
            roundedNumber = multiply;
        } else if (flag >= 0) {
            roundedNumber = number.divide(multiple, 0, RoundingMode.FLOOR).multiply(multiple);
        } else {
            roundedNumber = number.divide(multiple, RoundingMode.UP).multiply(multiple);
        }

        return roundedNumber;
    }

}
