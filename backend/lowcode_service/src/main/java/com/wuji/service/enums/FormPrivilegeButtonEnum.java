package com.wuji.service.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum FormPrivilegeButtonEnum {
    SUBMIT,
    SAVE,
    VIEW,
    EDIT,
    DELETE,
    EXPORT,
    IMPORT;

    public static List<String> getAllViewPrivilege() {
        return Lists.newArrayList(IMPORT.name(), EXPORT.name(), DELETE.name(), EDIT.name(), VIEW.name());
    }

    public static List<String> getAllOperatorPrivilege() {
        return Lists.newArrayList(SUBMIT.name(), SAVE.name());
    }
}
