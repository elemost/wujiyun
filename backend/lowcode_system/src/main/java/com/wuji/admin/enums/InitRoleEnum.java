package com.wuji.admin.enums;


import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum InitRoleEnum {
    ;

    private final Long id;
    private final String roleName;
    private final String roleKey;
    private final Integer roleSort;

    public static List<Long> getPullRole() {
        return new ArrayList<>();
    }
}
