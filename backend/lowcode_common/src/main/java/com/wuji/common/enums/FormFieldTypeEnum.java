package com.wuji.common.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum FormFieldTypeEnum {
    INPUT_TEXT("单行文本框", "input-text-wuji", "文本", "string"),
    TEXT_AREA("多行文本框", "textarea-wuji", "文本", "string"),
    INPUT_NUMBER("数字框", "input-number-wuji", "数字", "number"),
    INPUT_DATE("日期时间", "input-date-wuji", "时间戳","timestamp"),
    SELECT("单选下拉框", "select-wuji", "文本", "string"),
    TREE_SELECT("复选下拉框", "tree-select-wuji", "对象文本", "array"),
    TABS("选项卡", "tabs", "文本",""),
    DIVIDER("分隔线", "divider-wuji", "",""),
    INPUT_IMAGE("图片上传", "input-image-wuji", "对象","array"),
    INPUT_FILE("文件上传", "input-file-wuji", "对象","array"),
    SELECT_DATA("选择数据", "select-data-wuji", "对象","json"),
    ADDRESS_SELECTION("地址", "address-selection", "对象","json"),
    RICH_TEXT("富文本", "rich-text-wuji", "富文本",""),
    RADIOS("单选框", "radios-wuji", "文本","string"),
    CHECKBOXES("复选框", "checkboxes-wuji", "对象文本","array"),

    SUB_FORM_TYPE("子表单", "input-table-wuji", "列表","array"),
    FORM_INPUT_DEPT_SINGLE("部门单选", "input-dept-single", "列表","string"),
    FORM_INPUT_ROLE_SINGLE("角色单选", "input-role-single", "对象","string"),
    FORM_INPUT_DEPT_MULTIPLE("部门多选", "input-dept-multiple", "列表","array"),
    FORM_INPUT_USER_MULTIPLE("用户多选", "input-user-multiple", "列表","array"),
    FORM_INPUT_ROLE_MULTIPLE("角色多选", "input-role-multiple", "列表","array"),
    FORM_INPUT_USER_SINGLE("用户单选", "input-user-single", "列表","string"),
    SERIAL_NUMBER("流水号", "serial-number", "文本",""),
    // 聚合表特有
    FORM_INPUT_USER_SINGLE_USED("用户单选", "input-user-single-used", "列表",""),
    FORM_INPUT_DEPT_SINGLE_USED("部门单选", "input-dept-single-used", "列表",""),
    POSITION("定位组件","position-wuji", "对象",""),
    QUERY_DATA("查询数据", "query-data-wuji", "对象",""),
    SYSTEM_STATUS("系统状态", "system_status", "文本","string")
    ;

    private final String label;
    private final String fieldType;
    private final String dataType;
    private final String mappingType;

    public static List<String> defaultTitle() {
        return Lists.newArrayList(SERIAL_NUMBER.getFieldType(), INPUT_TEXT.getFieldType(), TEXT_AREA.getFieldType(),
                INPUT_NUMBER.getFieldType());
    }

    public static List<String> searchFieldType() {
        return Lists.newArrayList(SERIAL_NUMBER.getFieldType(), INPUT_TEXT.getFieldType(), TEXT_AREA.getFieldType(),TEXT_AREA.getFieldType(),
                INPUT_NUMBER.getFieldType(), SELECT.fieldType,RADIOS.getFieldType());
    }

    public static List<String> arrayFieldType() {
        return Lists.newArrayList(TREE_SELECT.getFieldType(), CHECKBOXES.getFieldType());
    }

    public static FormFieldTypeEnum getByFieldType(String fieldType) {
        return Arrays.stream(FormFieldTypeEnum.values()).filter(c -> fieldType.equals(c.getFieldType())).findFirst()
                .orElse(null);
    }

    public static List<String> getDeptFieldType() {
        return Lists.newArrayList(FORM_INPUT_DEPT_SINGLE.getFieldType(), FORM_INPUT_DEPT_MULTIPLE.getFieldType());
    }

    public static List<String> getDeptFieldTypeExistUse() {
        return Lists.newArrayList(FORM_INPUT_DEPT_SINGLE.getFieldType(), FORM_INPUT_DEPT_SINGLE_USED.getFieldType(),
                FORM_INPUT_DEPT_MULTIPLE.getFieldType());
    }

    public static List<String> getUserFieldType() {
        return Lists.newArrayList(FORM_INPUT_USER_SINGLE.getFieldType(), FORM_INPUT_USER_MULTIPLE.getFieldType());
    }

    public static List<String> getUserFieldTypeExistUse() {
        return Lists.newArrayList(FORM_INPUT_USER_SINGLE.getFieldType(), FORM_INPUT_USER_SINGLE_USED.getFieldType(),
                FORM_INPUT_USER_MULTIPLE.getFieldType());
    }

    public static List<String> getUserDeptFieldType() {
        return Lists.newArrayList(FORM_INPUT_USER_SINGLE.getFieldType(), FORM_INPUT_USER_MULTIPLE.getFieldType(),
                FORM_INPUT_DEPT_SINGLE.getFieldType(), FORM_INPUT_DEPT_MULTIPLE.getFieldType());
    }

    public static List<String> getRoleFieldType() {
        return Lists.newArrayList(FORM_INPUT_ROLE_SINGLE.getFieldType(), FORM_INPUT_ROLE_MULTIPLE.getFieldType());
    }

    public static Boolean checkUserField(String fieldType, String fieldName) {
        return FormFieldTypeEnum.getUserFieldType().contains(fieldType) || fieldName.equals("creator");
    }

    public static Boolean checkDeptField(String fieldType, String fieldName) {
        return FormFieldTypeEnum.getDeptFieldType().contains(fieldType);
    }

    public static List<String> notExport() {
        return Lists.newArrayList(SELECT_DATA.getFieldType(), DIVIDER.getFieldType(), RICH_TEXT.getFieldType(),
                INPUT_FILE.getFieldType(), INPUT_IMAGE.getFieldType(), POSITION.getFieldType(),
                QUERY_DATA.getFieldType());
    }

    public static List<String> notImport() {
        return Lists.newArrayList(SELECT_DATA.getFieldType(), DIVIDER.getFieldType(), RICH_TEXT.getFieldType(),
                INPUT_FILE.getFieldType(), INPUT_IMAGE.getFieldType(), POSITION.getFieldType(),
                QUERY_DATA.getFieldType());
    }
}
