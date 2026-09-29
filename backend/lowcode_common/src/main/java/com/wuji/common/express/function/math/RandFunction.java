package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

import java.util.Random;

public class RandFunction extends Operator {

    public RandFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        Random random = new Random();

        return random.nextDouble();
    }
}
