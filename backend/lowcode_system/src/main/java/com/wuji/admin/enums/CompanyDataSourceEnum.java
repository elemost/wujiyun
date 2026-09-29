package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompanyDataSourceEnum {
    DING_TALK("1"),
    DING_TALK_THIRD("1"),
    LARK("0"),
    WECOM("1"),
    WECOM_THIRD("1"),
    EXTERNAL_IMPORT("0");

    private final String rootParentId;
}
