package com.wuji.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleDefaultEnum {
    CURRENT_ROLE(9999L, "当前用户所处所有角色"),;

    private final Long id;

    private final String name;
}
