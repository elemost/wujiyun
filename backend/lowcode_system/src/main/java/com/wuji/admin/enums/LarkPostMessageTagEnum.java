package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LarkPostMessageTagEnum {
    TEXT("text"),
    A("a"),
    AT("at"),
    IMG("img"),
    ;

    private final String tag;
}
