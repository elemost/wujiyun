package com.wuji.common.express.function.high;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.Operator;
import com.wuji.common.model.info.FormDept;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class TextDeptFunction extends Operator {

    public TextDeptFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) {
        if (objects.length != 2) {
            return null;
        }
        Object object = objects[0];
        String key = objects[1].toString();
        List<FormDept> formDeptList = JSONArray.parseArray(JSONObject.toJSONString(object), FormDept.class);
        if (CollectionUtils.isEmpty(formDeptList)) {
            return null;
        }
        FormDept formDept = formDeptList.get(0);
        if ("deptId".equals(key)) {
            return formDept.getValue();
        } else if ("deptName".equals(key)) {
            return formDept.getLabel();
        }
        return null;
    }
}
