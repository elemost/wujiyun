package com.wuji.common.express.function.date;

import com.ql.util.express.Operator;

import java.util.Date;

public class NowFunction extends Operator {

    public NowFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {
        return new Date().getTime();
    }
}
