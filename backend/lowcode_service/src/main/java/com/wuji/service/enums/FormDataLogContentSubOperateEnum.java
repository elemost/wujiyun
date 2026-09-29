package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormDataLogContentSubOperateEnum {
    NEW("新建"),
    UPDATE("修改"),
    DELETE("删除");

    private final String action;
}
