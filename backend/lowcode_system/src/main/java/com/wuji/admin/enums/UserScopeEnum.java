package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserScopeEnum {
    USER("user", "用户"),
    DEPT("dept", "部门"),
    POST("post", "职位"),
    ;
    private final String type;

    private final String msg;
}
