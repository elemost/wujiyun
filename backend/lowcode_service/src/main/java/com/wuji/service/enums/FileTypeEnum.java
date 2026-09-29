package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileTypeEnum {
    FORM("form/"),
    LOGO("logo/"),
    OPEN_FILE("open/"),
    TEMPLATE("template/"),
    AUTH("auth/"),
    TEMPLATE_WORD("template/word/"),
    TEMPLATE_EXCEL("template/excel/");

    private final String path;
}
