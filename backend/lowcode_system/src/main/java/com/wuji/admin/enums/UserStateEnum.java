package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStateEnum {
    NORMAL("0"),
    FREEZE("2"),
    INVITE("3")
    ;

    private final String code;
}
