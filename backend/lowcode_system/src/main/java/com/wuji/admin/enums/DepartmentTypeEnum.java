package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DepartmentTypeEnum {
    INTERNAL_DEPT("00"),
    EXTERNAL_DEPT( "10");

    private final String code;

}
