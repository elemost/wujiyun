package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

public class UpperFunction extends Operator {

    public UpperFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) {

        StringBuilder sb = new StringBuilder();

        // 边界判断
        if (lists.length == 0) {
            return "";
        }

        Object param = lists[0];
        // 类型判断
        if (param instanceof String) {
            String str = (String) lists[0];
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (Character.isLowerCase(ch) && !Character.isDigit(ch)) {
                    ch = Character.toUpperCase(ch);
                }
                sb.append(ch);
            }

        } else {
            return "";
        }
        return sb.toString();
    }
}
