package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationInfoKeyEnum {
    EXPIRE_TIME("过期时间", "expireTime");

    private final String desc;

    private final String key;
}
