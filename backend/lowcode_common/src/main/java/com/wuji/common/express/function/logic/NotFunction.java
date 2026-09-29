package com.wuji.common.express.function.logic;

import com.ql.util.express.Operator;

public class NotFunction extends Operator {
    public NotFunction(String name) {
        this.name = name;
    }

    public NotFunction(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    public Object executeInner(Object[] list) throws Exception {
        return this.executeInner(list[0]);
    }

    public Object executeInner(Object op) throws Exception {
        if (op == null) {
            return Boolean.FALSE;
        } else if (Boolean.class.equals(op.getClass())) {
            return !(Boolean) op;
        } else {
            return Boolean.FALSE;
        }
    }
}
