package com.wuji.common.express.function.text;


import com.ql.util.express.Operator;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;

public class CharFunction extends Operator {
    public CharFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        // 参数校验
        if (list.length == 0) {
           return "";
        }
        // 获取参数
        Object obj = list[0];
        char c;
        if (obj instanceof Number) {
            c = (char) ((Number) obj).intValue();
        } else {
            throw new BizException(ResultCode.EXPRESS_FUNCTION_ERROR, "CHAR", "");
        }
        return c;
    }
}
