package com.wuji.service.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum ApplicationCategoryCategoryTypeEnum {
    FILE("文件夹"),
    FORM("表单页"),
    FORM_LIST("表单列表页"),
    FLOWABLE_FORM("流程表单"),
    FLOWABLE_FORM_LIST("流程表单列表页面"),
    DASH("仪表盘"),
    WEB("页面表单"),
    VIEW("视图"),
    ;
    private final String msg;

    /**
     * 存在表单数据的类型
     * @return
     */
    public static List<String> getExistFormList() {
        return Lists.newArrayList(FORM.name(), FORM_LIST.name(), FLOWABLE_FORM.name(), FLOWABLE_FORM_LIST.name());
    }

    /**
     * 表单类型
     * @return
     */
    public static List<String> getFormType() {
        return Lists.newArrayList(FORM.name(), FLOWABLE_FORM.name());
    }

    /**
     * 表单类型
     * @return
     */
    public static List<String> getAdminPrivilege() {
        return Lists.newArrayList(VIEW.name(),FORM.name(), FLOWABLE_FORM.name());
    }

    /**
     * 流程相关类型
     * @return
     */
    public static List<String> getFlowerFormType() {
        return Lists.newArrayList(FLOWABLE_FORM_LIST.name(), FLOWABLE_FORM.name());
    }

}
