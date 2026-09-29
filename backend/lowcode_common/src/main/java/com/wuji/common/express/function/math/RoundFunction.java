package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundFunction extends Operator {

    public RoundFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        // 边界判断
        if (lists.length == 0 || lists.length > 3) {
            return 0;
        }

        // 获取参数
        BigDecimal number = new BigDecimal(lists[0].toString());
        // 小数点位数
        int scale = Integer.parseInt(lists[1].toString());

        BigDecimal roundedNumber = number.setScale(scale, RoundingMode.HALF_UP);

        if (roundedNumber.stripTrailingZeros().scale() <= 0) {
            roundedNumber = roundedNumber.setScale(0);
        }

        if (roundedNumber.stripTrailingZeros().scale() >= 1 && scale >= roundedNumber.stripTrailingZeros().scale()) {
            roundedNumber = roundedNumber.stripTrailingZeros();
        }

        if (roundedNumber.stripTrailingZeros().scale() >= 1 && scale < roundedNumber.stripTrailingZeros().scale()) {
            roundedNumber = roundedNumber.setScale(0);
        }

        return roundedNumber;
    }
}
