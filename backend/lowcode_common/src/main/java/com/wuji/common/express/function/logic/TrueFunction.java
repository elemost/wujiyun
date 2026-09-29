package com.wuji.common.express.function.logic;

import com.ql.util.express.Operator;


public class TrueFunction extends Operator {

    public TrueFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return Boolean.TRUE;
    }
}
