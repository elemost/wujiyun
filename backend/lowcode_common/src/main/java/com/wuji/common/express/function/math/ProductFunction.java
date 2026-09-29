package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

import java.math.BigDecimal;

public class ProductFunction extends Operator {

    public ProductFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length == 0) {
            return 0;
        }

        Object result = BigDecimal.ONE;

        for (Object tmp : list) {
            result = OperatorOfNumber.multiply(result, tmp, this.isPrecise);
        }

        return result;
    }
}
