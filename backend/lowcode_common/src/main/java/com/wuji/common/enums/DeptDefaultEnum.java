package com.wuji.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeptDefaultEnum {
    CURRENT_DEPT(9999L, "当前用户所处所有部门"),
    CURRENT_ALL_DEPT(-9996L, "当前用户所处所有部门及部门以下"),
    CURRENT_PARENT_DEPT(-9995L, "当前用户所处所有部门及其父级以下");;

    private final Long id;

    private final String name;
}
