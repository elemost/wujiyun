package com.wuji.common.express.function.high;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.Operator;

import java.util.ArrayList;
import java.util.List;

public class IndexFunction extends Operator {

    public IndexFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) {
        if (objects.length != 2) {
            return null;
        }
        Object object = objects[0];
        if (!(object instanceof ArrayList)) {
            return null;
        }
        Integer index = (Integer) objects[1];
        List<Object> objs = JSONArray.parseArray(JSONObject.toJSONString(object));
        if (objs.size() < index) {
            return null;
        }
        if (index == -1) {
            return objs.get(objs.size() - 1);
        }
        return objs.get(index - 1);
    }
}
