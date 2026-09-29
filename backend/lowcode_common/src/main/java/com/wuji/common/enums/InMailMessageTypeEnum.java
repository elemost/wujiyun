package com.wuji.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InMailMessageTypeEnum {
    DOSAGE_REMINDER("用量提醒"),
    VIP_EXPIRE("会员过期"),
    APPLICATION_EXPIRE("应用过期"),
    FLOWABLE_NOTICE("流程引擎待办消息");

    private final String msg;
}
