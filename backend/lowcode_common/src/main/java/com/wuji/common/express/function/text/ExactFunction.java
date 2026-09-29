package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

public class ExactFunction extends Operator {

    public ExactFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {

        if (list.length != 2) {
            return false;
        }
        String param1 = (String) list[0];
        String param2 = (String) list[1];

        return param1.equals(param2);
    }
}
