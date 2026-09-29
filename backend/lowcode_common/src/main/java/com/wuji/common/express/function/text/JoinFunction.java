package com.wuji.common.express.function.text;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.Operator;

import java.lang.reflect.Array;
import java.util.List;


public class JoinFunction extends Operator {

    public JoinFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) {
        if (list.length == 0) {
            return "";
        }
        Object rst = null;
        Object arr = list[0];
        if ((arr.getClass().isArray() || arr instanceof List)) {
            StringBuilder sb = new StringBuilder();
            if (arr.getClass().isArray()) {
                int len = Array.getLength(arr);
                Object opt = list[list.length - 1];
                for (int i = 0; i < len; i++) {
                    sb.append(Array.get(arr, i)).append(opt);
                }
                rst = sb.substring(0, sb.length() - 1);
            } else {
                List<Object> objs = JSONArray.parseArray(JSONObject.toJSONString(arr));
                Object opt = list[list.length - 1];
                for (Object obj : objs) {
                    sb.append(obj).append(opt);
                }
                rst = sb.substring(0, sb.length() - 1);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            // 操作符号
            Object opt = list[list.length - 1];
            // 循环
            for (int i = 0; i < list.length - 1; i++) {
                sb.append(list[i]).append(opt);
            }

            rst = sb.substring(0, sb.length() - 1);
        }
        return rst;
    }
}
