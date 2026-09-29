package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  MenuTypeEnum {
    M("目录"),
    C("菜单"),
    F("按钮")
    ;
    private final String desc;
}
