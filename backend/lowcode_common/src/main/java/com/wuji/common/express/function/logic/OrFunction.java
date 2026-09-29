package com.wuji.common.express.function.logic;

import com.ql.util.express.Operator;

public class OrFunction extends Operator {
    public OrFunction(String name) {
        this.name = name;
    }

    public OrFunction(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    public Object executeInner(Object[] list) throws Exception {
        return this.executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        boolean r1;
        if (op1 == null) {
            r1 = false;
        } else {
            if (!(op1 instanceof Boolean)) {
                return Boolean.FALSE;
            }

            r1 = (Boolean) op1;
        }
        boolean r2;
        if (op2 == null) {
            r2 = false;
        } else {
            if (!(op2 instanceof Boolean)) {
                return Boolean.FALSE;
            }

            r2 = (Boolean) op2;
        }
        return r1 || r2;
    }
}
