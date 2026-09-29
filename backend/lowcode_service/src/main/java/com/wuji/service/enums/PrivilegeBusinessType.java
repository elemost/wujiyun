package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PrivilegeBusinessType {
    USER("user", "用户"),
    DEPT("dept", "部门"),
    POST("post", "职位");
    private final String type;
    private final String description;
}
