package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AdminResultCode {
    PASSWORD_ERROR("1001", 1008, "账号密码错误"),
    USER_NAME_EXIST("1002", 1009, "用户已存在"),
    PHONE_NUMBER_EXIST("1009", 1016, "手机号已存在"),
    DEPT_NAME_EXIST("1003", 1010, "部门已存在"),
    IMPORT_LIST_ERROR("1004", 1011, "导入人员失败:%s"),
    POST_NAME_EXIST("1005", 1012, "职位已存在"),
    EXCEL_NAME_SAME("1006", 1013, "excel中手机号码重复：%s"),
    DING_TALK_CLIENT_ERROR("1007", 1014, "获取钉钉client失败"),
    DING_TALK_TOKEN_ERROR("1008", 1015, "获取钉钉TOKEN失败"),
    DECRYPT("1009", 1016, "解密失败"),
    SEND_MESSAGE_FAIL("1010", 1017, "发送短信失败"),
    SEND_MESSAGE_LIMIT("1011", 1018,   "请在1分钟后重试"),
    SEND_MESSAGE_REPEAT("1012", 1019,   "请勿重复发送验证码！"),
    MESSAGE_CODE_ERROR("1013", 1020,   "短信验证码无效"),
    MESSAGE_TEMPLATE_NOT_EXIST("1014", 1021, "短信场景%s模板不存在"),
    COMPANY_NAME_IS_EMPTY("1015", 1022, "公司名称必须提供"),
    USER_HAS_COMPANY("1016", 1023, "当前用户已有归属公司，不能创建"),
    COMPANY_EXIST("1017", 1024,  "公司已存在，请更换公司名称"),
    MOBILE_BIND("1018", 1025,  "该手机号已被其他微信号绑定！"),
    ADMIN_DELETE_FAIL("1019", 1026,  "当前用户为公司创建人，不可删除！"),
    USER_NAME_NOT_EXIST("1020", 1027, "用户不存在"),
    USER_LIMIT("1021", 1028, "当前企业用户数量限制%s人!"),
    PHONE_NUMBER_ERROR("1022", 1029, "手机号码错误"),
    USER_NAME_NOY_EXIST_COMPANY("1023", 1030, "当前用户不属于该公司"),
    ORIGIN_PASSWORD_ERROR("1024", 1031, "原密码错误"),
    COMPANY_NOT_AUTH("1025", 1032, "您的企业微信还未授权安装五极云"),
    PHONE_NO_AUTH_OR_NOT_EXIST("1026", 1033, "对应用户没有对应企业微信应用权限:%s"),
    WECOM_HAS_BEEN_BIND("1027", 1034, "该手机号对应的企业微信已被其他手机号绑定"),
    FREE_PRODUCT_NOT_BUY_AGAIN("1028", 1035, "免费商品不能重复购买"),
    PAY_ERROR("1029", 1036, "支付异常，请重新再试！"),
    COMPANY_NOT_EXIST("1030", 1037, "公司不存在"),
    USER_NOT_EXIST("1031", 1038, "用户不存在"),
    ;

    private final String code;
    private final Integer subCode;
    private final String message;
}
