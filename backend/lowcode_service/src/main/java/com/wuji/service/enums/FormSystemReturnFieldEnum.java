package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormSystemReturnFieldEnum {
    BUTTON("按钮", "system", "button", "button"),
    PRIVILEGE("权限", "system", "privilege", "privilege"),
    CREATOR("系统用户","system","system_creator", "creator"),
    TASK_NAME("系统用户","system","system_taskName", "taskName"),
    LOCK("锁定","system","lock", "lock"),
    ;

    private final String label;

    private final String type;

    private final String name;

    private final String alias;
}
