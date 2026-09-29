package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

import java.util.Arrays;

public class ConcatenateFunction extends Operator {

    public ConcatenateFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {

        if (list.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Arrays.stream(list).forEach(c -> sb.append(c != null ? c : ""));
        return sb.toString();
    }
}