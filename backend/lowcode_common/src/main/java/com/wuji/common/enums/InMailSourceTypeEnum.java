package com.wuji.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InMailSourceTypeEnum {
    SYSTEM("系统消息"),
    COMPANY("公司创建"),
    DATA_STREAM("数智助手");

    private final String msg;
}
