package com.wuji.common.express.function.logic;

import com.ql.util.express.Operator;

public class AndFunction extends Operator {
    public AndFunction(String name) {
        this.name = name;
    }

    public Object executeInner(Object[] list) throws Exception {
        return this.executeInner(list[0], list[1]);
    }

    public Object executeInner(Object operand1, Object operand2) throws Exception {
        boolean r1;
        if (operand1 == null) {
            r1 = false;
        } else {
            if (!(operand1 instanceof Boolean)) {
                return Boolean.FALSE;
            }
            r1 = (Boolean) operand1;
        }
        boolean r2;
        if (operand2 == null) {
            r2 = false;
        } else {
            if (!(operand2 instanceof Boolean)) {
                return Boolean.FALSE;
            }
            r2 = (Boolean) operand2;
        }
        return r1 && r2;
    }
}
