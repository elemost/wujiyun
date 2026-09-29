package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

public class AvgFunction extends Operator {

    public AvgFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        // 参数校验
        if (objects.length == 0) {
            return 0;
        }
        Object result = 0;
        int len = objects.length;
        for (Object object : objects) {
            // isPrecise true 代表高精度计算，false代表普通计算
            result = OperatorOfNumber.add(result, object, isPrecise);
        }
        // 求平均值
        result = OperatorOfNumber.divide(result, len, isPrecise);
        return result;
    }
}
