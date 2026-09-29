package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;


public class LowerFunction extends Operator {

    public LowerFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length != 1) {
            return "";
        }
        Object text = list[0];
        String textStr = "";
        if (text instanceof String) {
            textStr = (String) text;
        } else {
            return "";
        }
        return textStr.toLowerCase();
    }
}
