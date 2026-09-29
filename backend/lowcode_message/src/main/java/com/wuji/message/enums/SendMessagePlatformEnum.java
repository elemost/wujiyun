package com.wuji.message.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SendMessagePlatformEnum {
    LARK,
    DING_TALK,
    WECOM,
    IN_MAIL,
    WECOM_THIRD,
}
