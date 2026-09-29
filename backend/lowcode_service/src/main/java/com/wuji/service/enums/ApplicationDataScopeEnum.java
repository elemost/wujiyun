package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationDataScopeEnum {
    MY_CREATE("我创建的");

    private final String msg;
}
