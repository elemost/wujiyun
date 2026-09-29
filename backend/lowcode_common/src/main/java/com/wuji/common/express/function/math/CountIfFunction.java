package com.wuji.common.express.function.math;


import com.ql.util.express.Operator;

import java.util.Arrays;
import java.util.function.Predicate;

public class CountIfFunction extends Operator {

    public CountIfFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {

        // 参数校验
        if (objects.length == 0) {
            return 0;
        }

        Predicate<Object> predicate = null;

        // 截取条件
        String criteria = objects[objects.length-1].toString();

        String op = criteria.replaceAll("[0-9]", "");

        // 值
        Object[] values = copyV(objects);

        if (">".equals(op)) {
            // 提取数字值
            String conditionV = criteria.replaceAll("[^0-9]", "");
            predicate = object -> {

                double param = Double.parseDouble(object.toString());
                double p = Double.parseDouble(conditionV);

                return param > p;
            };

        } else if ("<".equals(op)) {// >=
            // 提取数字值
            String conditionV = criteria.replaceAll("[^0-9]", "");
            predicate = object -> {
                double param = Double.parseDouble(object.toString());
                double p = Double.parseDouble(conditionV);
                return param < p;
            };
        } else if ("<=".equals(op)) {// >=
            // 提取数字值
            String conditionV = criteria.replaceAll("[^0-9]", "");
            predicate = object -> {
                double param = Double.parseDouble(object.toString());
                double p = Double.parseDouble(conditionV);
                return param <= p;
            };
        } else if (">=".equals(op)) {
            // 提取数字值
            String conditionV = criteria.replaceAll("[^0-9]", "");
            predicate = object -> {

                double param = Double.parseDouble(object.toString());
                double p = Double.parseDouble(conditionV);

                return param >= p;
            };

        } else if ("!=".equals(op)) {
            // 提取数字值
            String conditionV = criteria.replaceAll("[^0-9]", "");
            predicate = object -> {

                double param = Double.parseDouble(object.toString());
                double p = Double.parseDouble(conditionV);

                return param != p;
            };

        } else if ("==".equals(op)) {
            // 提取数字值
            String conditionV = criteria.replaceAll("[^0-9]", "");
            predicate = object -> {

                double param = Double.parseDouble(object.toString());
                double p = Double.parseDouble(conditionV);

                return param == p;
            };
        } else {
            predicate = object -> object.toString().equals(criteria);
        }
        return Arrays.stream(values).filter(predicate).count();
    }

    public Object[] copyV(Object[] src) {

        Object[] rst = new Object[src.length-1];

        System.arraycopy(src, 0, rst, 0, rst.length);

        return rst;

    }

}
