package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormPrivilegeUserPrivilegeEnum {
    ALL("全部人员"),
    CUSTOM("自定义");

    private final String msg;
}
