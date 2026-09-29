package com.wuji.common.express.function.math;


import com.ql.util.express.Operator;

public class SqrtFunction extends Operator {

    public SqrtFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        // 边界判断
        if (lists.length != 1) {
            return 0;
        }

        Object rst = null;
        // 入参 固定数值 数值字段
        Object obj = lists[0];
        if (obj instanceof Number) {
            // 转成double 处理
            double p = ((Number) obj).doubleValue();
            rst = Math.sqrt(p);
        }

        return rst;
    }
}
