package com.wuji.common.express.function.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.Operator;
import com.wuji.common.model.info.FormUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRemoveFunction extends Operator {

    public UserRemoveFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length < 2) {
            return new ArrayList<>();
        }
        List<FormUser> formUserList = JSONArray.parseArray(JSONObject.toJSONString(list[0]), FormUser.class).stream()
                .peek(c -> c.setAssigneeName("")).collect(Collectors.toList());
        for (int i = 1; i < list.length; i++) {
            if (list[1] == null) {
                return formUserList;
            }
            List<FormUser> removeList = JSONArray.parseArray(JSONObject.toJSONString(list[1]), FormUser.class).stream()
                    .peek(c -> c.setAssigneeName("")).collect(Collectors.toList());
            formUserList.removeAll(removeList);
        }
        return formUserList;
    }
}
