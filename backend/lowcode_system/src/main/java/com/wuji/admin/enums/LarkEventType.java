package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LarkEventType {
    DEPT_CREATE("contact.department.created_v3"),
    DEPT_UPDATE("contact.department.updated_v3"),
    DEPT_DELETE("contact.department.deleted_v3"),
    USER_CREATE("contact.user.created_v3"),
    USER_UPDATE("contact.user.updated_v3"),
    USER_DELETE("contact.user.deleted_v3")
    ;

    private final String type;
}
