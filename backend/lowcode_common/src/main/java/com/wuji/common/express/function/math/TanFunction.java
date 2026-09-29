package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

public class TanFunction extends Operator {

    public TanFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        // 参数校验
        if (lists.length == 0) {
            return 0;
        }

        double hudu = Double.parseDouble(lists[0].toString());

        return Math.tan(hudu);
    }
}
