package com.wuji.framework.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FrameworkResultCode {
    PASSWORD_ERROR("1001", 1008, "账号密码错误"),
    USER_NAME_EXIST("1002", 1009, "账号已存在");

    private final String code;
    private final Integer subCode;
    private final String message;
}
