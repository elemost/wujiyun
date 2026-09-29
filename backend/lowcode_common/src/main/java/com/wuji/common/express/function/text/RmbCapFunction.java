package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;
import com.wuji.common.utils.RmbCapConverter;

import java.math.BigDecimal;

public class RmbCapFunction extends Operator {

    public RmbCapFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        String rst = "";
        if (list.length == 0) {
            throw new QLException("操作数异常");
        } else if (list.length != 1) {
            throw new QLException("操作数个数异常");
        } else {
            //数字
            Object num = list[0];

            BigDecimal numB = new BigDecimal(num.toString());
            double numD = numB.doubleValue();
            rst = RmbCapConverter.convertToRmbCap(numD);

        }
        return rst;
    }
}
