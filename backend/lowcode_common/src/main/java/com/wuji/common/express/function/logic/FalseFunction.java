package com.wuji.common.express.function.logic;

import com.ql.util.express.Operator;


public class FalseFunction extends Operator {

    public FalseFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return Boolean.FALSE;
    }
}
