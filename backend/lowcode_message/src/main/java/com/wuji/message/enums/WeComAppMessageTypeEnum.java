package com.wuji.message.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WeComAppMessageTypeEnum {
    TEXT("text"),
    MARKDOWN("markdown");

    private final String type;
}
