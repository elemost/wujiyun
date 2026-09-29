package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UnionFunction extends Operator {

    public UnionFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] lists) throws Exception {

        if (lists.length == 0) {
            return "";
        }

        Set<Object> rst = new HashSet<>();
        for (Object obj : lists) {
            if ((obj.getClass().isArray() || obj instanceof List)) {
                if (obj.getClass().isArray()) {
                    int len = Array.getLength(obj);
                    for (int i = 0; i < len; i++) {
                        rst.add(Array.get(obj, i));
                    }
                } else {
                    @SuppressWarnings("unchecked") List<Object> array = (List<Object>) obj;
                    rst.addAll(array);
                }

            } else {
                rst.add(obj);
            }
        }
        return rst;
    }

    public Set<Object> union(Object... texts) {
        Set<Object> resultSet = new HashSet<>();
        resultSet.addAll(Arrays.asList(texts));
        return resultSet;
    }
}
