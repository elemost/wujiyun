package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationStateEnum {
    DOWN("未启用"),
    UP("已启用");

    private final String msg;
}
