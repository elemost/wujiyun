package com.wuji.service.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum SearchMethodEnum {
    EQ("eq", "等于"),
    NE("ne","不等于"),
    IN("in", "属于"),
    NI("ni", "不属于"),
    UL("ul", "为空"),
    NU("nu", "不为空"),
    LK("lk", "字符包含"),
    BTS("bts", "字符属于"),
    UK("uk", "字符不包含"),
    GT("gt", "大于"),
    LT("lt", "小于"),
    LE("le", "小于或等于"),
    GE("ge", "大于或等于"),
    BT("bt", "数组包含任意一个"),
    UBT("ubt", "数组不包含任意一个"),
    ALL("all", "同时包含"),
    EMPTY("empty", "数组为空"),
    UN_EMPTY("unEmpty", "数组不为空"),
    RANGE("range", "范围内"),
    NULL("null", "为null"),
    NU_NULL("nuNull", "为unNull"),
    FORMULA("formula", "函数"),
    DATE_CUSTOM("dateCustom", "自定义日期偏移类型"),
    USER_DEPT("userDept", "用户部门"),
    USER_DEPT_CHILD("userDeptChild", "用户部门及子部门"),
    USER_POST("userPost", "用户岗位"),
    ;
    private final String method;

    private final String message;

    public static List<String> valueIsEmpty() {
        return Lists.newArrayList(NULL.name(), NU_NULL.name(), FORMULA.name(), UN_EMPTY.name(), UL.name(), NU.name(), EMPTY.name());
    }

}
