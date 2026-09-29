package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.FunctionException;
import org.apache.commons.lang3.math.NumberUtils;

public class AbsFunction extends Operator {

    public AbsFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length == 0) {
            return null;
        }
        // 取出来数据
        Object result = list[0];
        if (!NumberUtils.isCreatable(result.toString())) {
            throw new FunctionException(ResultCode.FORMULA_ERROR, "当前值不为数字：" + result);
        }
        if (result instanceof Integer) {
            int val = (Integer) result;
            // 调用Math函数提供的取绝对值的方法
            result = Math.abs(val);
        } else if (result instanceof Double) {
            double val = (Double) result;
            result = Math.abs(val);
        } else if (result instanceof Float) {
            double val = (Float) result;
            result = Math.abs(val);
        } else if (result instanceof Long) {
            long val = (Long) result;
            result = Math.abs(val);
        } else if (result instanceof Short) {
            short val = (Short) result;
            result = Math.abs(val);
        } else {
            return 0;
        }
        return result;
    }
}
