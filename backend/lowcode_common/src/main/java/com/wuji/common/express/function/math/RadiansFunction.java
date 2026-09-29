package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

public class RadiansFunction extends Operator {

    public RadiansFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {

        // 参数校验
        if (objects.length == 0) {
            return 0;
        }

        Object param = objects[0];
        double pv = Double.parseDouble(param.toString());
        return Math.toRadians(pv);
    }
}
