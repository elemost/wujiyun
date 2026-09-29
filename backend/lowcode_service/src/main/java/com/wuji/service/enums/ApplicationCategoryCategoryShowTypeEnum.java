package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationCategoryCategoryShowTypeEnum {
    SHOW("显示"),
    ONLY_WEB_SHOW("web端显示"),
    HIDE("隐藏")
    ;
    private final String msg;
}
