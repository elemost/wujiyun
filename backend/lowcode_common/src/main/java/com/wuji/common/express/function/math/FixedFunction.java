package com.wuji.common.express.function.math;


import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FixedFunction extends Operator {

    public FixedFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length != 2) {
            return 0;
        }
        int decimals = 0;
        if (list[1] instanceof Integer) {
            decimals = Integer.parseInt(list[1].toString());
        } else {
            return 0;
        }
        BigDecimal number = new BigDecimal(list[0].toString());
        BigDecimal result = roundToDecimals(number, decimals);
        return formatNumber(result, decimals);
    }

    public static BigDecimal roundToDecimals(BigDecimal number, int decimals) {
        return number.setScale(decimals, RoundingMode.HALF_UP);
    }

    public static String formatNumber(BigDecimal number, int decimals) {
        StringBuilder zeros = new StringBuilder();
        for (int i = 0; i < decimals; i++) {
            zeros.append("0");
        }
        DecimalFormat decimalFormat = new DecimalFormat("#." + zeros.toString());
        return decimalFormat.format(number);
    }
}
