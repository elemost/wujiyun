package com.wuji.open.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OpenResultCode {
    APP_KEY_NOT_EXIST("6001", 6001, "密钥不存在"),
    APP_KEY_CLOSE("6002", 6002, "密钥已被停用"),
    FORM_MAPPING_NOT_EXIST("6003", 6003, "当前表单参数别名未设置，请前往开放平台参数管理进行配置"),
    DATA_NOT_EXIST("6004", 6004, "当前修改的数据不存在"),
    CURRENT_USER_NOT_EXIST("6005", 6005, "当前用户不存在"),
    SYNC_ERROR("6006", 6006, "数据同步失败：%s"),
    ;

    private final String code;
    private final Integer subCode;
    private final String message;
}
