package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompanyInfoKeyEnum {
    LOGO("logo", Boolean.TRUE),
    LOGIN_LOGO("loginLogo",Boolean.TRUE),
    BACKGROUND("background",Boolean.TRUE),
    SUBJECT_COLOR("subjectColor",Boolean.TRUE),
    COMPANY_KEY("companyKey",Boolean.FALSE),
    COMPANY_SECRET("companySecret",Boolean.FALSE),
    COMPANY_TOKEN("companyToken",Boolean.FALSE),
    userLimit("userLimit", Boolean.TRUE)
    ;


    private final String key;

    private final Boolean keyExist;
}
