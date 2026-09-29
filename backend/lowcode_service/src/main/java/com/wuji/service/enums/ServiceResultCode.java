package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceResultCode {
    APPLICATION_NAME_EXIST("3001", 3001, "应用名称已存在"),
    DATA_NOT_EXIST("3002", 3002, "数据不存在"),
    QUOTED("3003", 3003, "该表单被引用不可删除 %s"),
    FORMULA_ERROR("3004", 3004, "%公式错误 %s"),
    CURRENT_CATEGORY_DELETED_FAIL("3005", 3005, "非空文件夹不可删除"),
    DATA_SAME_EXIST("3006", 3006, "表单[%s]字段不可重复，请修改后重新提交，重复值分别为：[%s]"),
    NOT_PERMISSION("3007", 3007, "暂无权限"),
    PARAM_ERROR ("3008", 3008, "参数错误:%s"),
    SIGNATURE_ERROR("3009", 3009, "签名失败"),
    TEMPLATE_EXPORT_ERROR("3010", 3010, "模板导出失败"),
    WORD_ANALYSIS_ERROR("3011", 3011, "WORD解析失败"),
    MANAGE_SAME_USR_ID("3012", 3012, "当前用户已在其他权限组：%s"),
    DATA_STREAM_CALCULATE("3013", 3013, "计算节点执行失败：%s"),
    FORM_SUBMIT_RULE_ERROR("3014", 3014, "%s"),
    FORM_DATA_FACTORY_CONFIG_ERROR("3015", 3015, "%s"),
    FORM_DATA_STREAM_DELETED("3016", 3016, "数据助手已被删除"),
    APPLICATION_SHARE_EXPIRE("3017", 3017, "应用分享链接已过期"),
    APPLICATION_SHARE_NOT_EXIST("3018", 3018, "应用分享不存在");

    private final String code;
    private final Integer subCode;
    private final String message;
}
