package com.wuji.common.express.function.math;

import com.ql.util.express.Operator;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SmallFunction extends Operator {

    public SmallFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length != 2) {
            return null;
        }

        Object arr = list[0];
        BigDecimal[] array = null;

        if ((arr.getClass().isArray() || arr instanceof List)) {
            if (arr.getClass().isArray()) {
                int len = Array.getLength(arr);
                array = new BigDecimal[len];
                for (int i = 0; i < len; i++) {
                    array[i] = new BigDecimal(Array.get(arr, i).toString());
                }
            } else {
                List<Object> objs = (List<Object>) arr;
                array = new BigDecimal[objs.size()];
                for (int i = 0; i < objs.size(); i++) {
                    array[i] = new BigDecimal(objs.get(i).toString());
                }
            }
        }

        int k = Integer.parseInt(list[1].toString());
        return getKthSmall(array, k);
    }

    public static BigDecimal getKthSmall(BigDecimal[] array, int k) {
        List<BigDecimal> list = Arrays.asList(array);
        Collections.sort(list);
        return list.get(k - 1);
    }
}
