package com.wuji.common.express.function.math;


import com.ql.util.express.Operator;

public class CountFunction extends Operator {

    public CountFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {

        return objects.length;
    }
}
