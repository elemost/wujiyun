package com.wuji.common.model.info;

import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class FormUser implements Serializable {

    private static final long serialVersionUID = 3670079982654483072L;

    private String assigneeName;

    private Long assigneeId;

    public static FormUser getCurrent(UserDomain userDomain) {
        if (userDomain == null) {
            userDomain = UserUtils.getUser();
        }
        FormUser user = new FormUser();
        user.setAssigneeName(userDomain.getNickName());
        user.setAssigneeId(Long.valueOf(userDomain.getUserId()));
        return user;
    }

    public static List<FormUser> getDefaultUser() {
        List<FormUser> list = new ArrayList<>();
        FormUser user = new FormUser();
        user.setAssigneeName("当前用户");
        user.setAssigneeId(9999L);
        list.add(user);
        return list;
    }

    public static List<Object> getDefaultUserObject() {
        List<Object> list = new ArrayList<>();
        FormUser user = new FormUser();
        user.setAssigneeName("当前用户");
        user.setAssigneeId(9999L);
        list.add(user);
        return list;
    }
}
