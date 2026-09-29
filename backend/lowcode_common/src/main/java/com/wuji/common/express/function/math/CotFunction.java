package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

public class CotFunction extends Operator {

    public CotFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {

        // 参数校验
        if (objects.length == 0) {
            return 0;
        }

        double hudu = Double.parseDouble(objects[0].toString());

        return 1/Math.tan(hudu);
    }
}
