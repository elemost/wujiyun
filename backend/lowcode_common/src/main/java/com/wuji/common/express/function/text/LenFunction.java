package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

public class LenFunction extends Operator {

    public LenFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length != 1) {
            return 0;
        }
        Object text = list[0];
        String textStr = "";
        if (text instanceof String) {
            textStr = (String) text;
        } else {
            return 0;
        }

        return textStr.length();
    }
}
