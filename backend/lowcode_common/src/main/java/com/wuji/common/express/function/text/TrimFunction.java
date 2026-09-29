package com.wuji.common.express.function.text;


import com.ql.util.express.Operator;

public class TrimFunction extends Operator {

    public TrimFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        String rst = "";
        if (list.length != 1) {
            return "";
        }
        Object text = list[0];
        if (text instanceof String) {
            String textS = text.toString();
            textS = textS.trim();
            // 缩减内部连续多个空格至一个空格
            rst = textS.replaceAll("\\s+", " ");
        } else {
            return "";
        }
        return rst;
    }
}
