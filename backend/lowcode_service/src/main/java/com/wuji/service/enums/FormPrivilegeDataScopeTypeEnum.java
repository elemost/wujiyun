package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormPrivilegeDataScopeTypeEnum {
    ALL,
    OWNER,
    OWNER_DEPT,
    CUSTOM_DEPT,
    CUSTOM_FILTER;
}
