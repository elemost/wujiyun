package com.wuji.common.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum UserDefaultEnum {
    SYSTEM_USER(-9996L, ""),
    NO_BODY(-9997L, "匿名用户"),
    ANONYMOUS_USER(-9998L, "匿名用户"),
    CURRENT_USER(9999L, "当前用户");

    private final Long id;

    private final String name;

    public static List<UserDefaultEnum> systemUser() {
        return Lists.newArrayList(CURRENT_USER);
    }
}
