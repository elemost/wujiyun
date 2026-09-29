package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

public class SumFunction extends Operator {

    public SumFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        if (lists.length == 0) {
            return 0;
        }

        Object result = 0;
        for (Object list : lists) {
            result = OperatorOfNumber.add(result, list, isPrecise);
        }
        return result;
    }
}
