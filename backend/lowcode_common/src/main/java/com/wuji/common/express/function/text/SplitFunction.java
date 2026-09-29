package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

import java.util.Arrays;
import java.util.List;

public class SplitFunction extends Operator {

    public SplitFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        List<Object> objects = null;
        if (list.length != 2) {
            return objects;
        } else {
            // 判断数据类型
            Object text = list[0];
            Object text_separator = list[1];
            objects = splitText((String) text, (String) text_separator);
        }
        return objects;
    }

    public static <T> List<T> splitText(String text, String delimiter) {
        String[] arr = text.split(delimiter);
        return Arrays.asList((T[]) arr);
    }
}
