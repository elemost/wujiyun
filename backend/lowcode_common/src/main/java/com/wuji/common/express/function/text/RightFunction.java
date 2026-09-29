package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

public class RightFunction extends Operator {

    public RightFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length == 0) {
            return "";
        }

        Object text = list[0];
        Object index = list[1];
        String textStr = "";
        int inx = 0;
        if (text instanceof String && index instanceof Integer) {
            textStr = (String) text;
            inx = (int) list[1];
        } else {
            return "";
        }

        if (inx > textStr.length()) {
            inx = textStr.length();
        }

        int len = textStr.length();
        int startInx = len - inx;

        return textStr.substring(startInx, len);
    }
}
