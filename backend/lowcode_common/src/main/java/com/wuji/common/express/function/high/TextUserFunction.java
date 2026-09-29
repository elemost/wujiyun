package com.wuji.common.express.function.high;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.Operator;
import com.wuji.common.model.info.FormUser;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class TextUserFunction extends Operator {

    public TextUserFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] objects) {
        if (objects.length != 2) {
            return null;
        }
        Object object = objects[0];
        String key = objects[1].toString();
        List<FormUser> formUserList = JSONArray.parseArray(JSONObject.toJSONString(object), FormUser.class);
        if (CollectionUtils.isEmpty(formUserList)) {
            return null;
        }
        FormUser formUser = formUserList.get(0);
        if ("userId".equals(key)) {
            return formUser.getAssigneeId();
        } else if ("userName".equals(key)) {
            return formUser.getAssigneeName();
        }
        return "";
    }
}
