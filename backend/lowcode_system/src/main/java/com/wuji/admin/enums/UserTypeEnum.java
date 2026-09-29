package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserTypeEnum {
    INTERNAL("00"),
    EXTERNAL( "10");

    private final String code;
}
