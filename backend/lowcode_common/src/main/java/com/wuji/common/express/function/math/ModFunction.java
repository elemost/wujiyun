package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

import java.math.BigDecimal;

public class ModFunction extends Operator {

    public ModFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {


        if (lists.length == 0 || lists.length > 2) {
            return 0;
        }

        Object o1 = lists[0];
        Object o2 = lists[1];

        if (o1 instanceof BigDecimal) {
            BigDecimal dividend = new BigDecimal(o1.toString());

            BigDecimal divisor = new BigDecimal(o2.toString());

            return dividend.remainder(divisor);
        } else {
            return OperatorOfNumber.modulo(o1, o2);
        }
    }
}
