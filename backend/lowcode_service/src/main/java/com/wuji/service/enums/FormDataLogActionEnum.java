package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormDataLogActionEnum {
    NEW("新建"),
    SUBMIT("提交"),
    SAVE("保存");

    private final String action;
}
