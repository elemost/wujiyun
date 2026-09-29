package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

public class MinFunction extends Operator {

    public MinFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length == 0) {
            return null;
        } else {
            Object result = list[0];

            for (int i = 1; i < list.length; ++i) {
                result = this.executeInner(result, list[i]);
            }

            return result;
        }
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        Object result = null;
        int compareResult = Operator.compareData(op1, op2);
        if (compareResult < 0) {
            result = op1;
        } else {
            result = op2;
        }
        return result;
    }
}
