package com.wuji.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ConfigEnum {
    LOWCODE_MENU_ID("低代码平台菜单id",""),
    LOWCODE_ROLE_ID("低代码平台角色id",""),
    SYSTEM_ROLE_COMPANY("系统角色公司Id",""),
    LOWCODE_DEFAULT_PASSWORD("低代码平台默认密码","elemost@123"),
    LOWCODE_PUBLIC_PUBLISH_URL("低代码平台地址","https://cloud.elemost.com/"),
    LOWCODE_EMAIL_CONFIG("邮箱配置",""),
    LOWCODE_PROFILE("官网环境url","https://www.elemost.com/"),
    APPLICATION_EXPIRE_DAY("应用过期时间天数", "30"),
    APPLICATION_SHARE_EXPIRE_DAY("应用分享时间天数", "7"),
    LOWCODE_START_TIME("", ""),
    LOCATION_COMPONENT_KEY("天地地图key",""),
    LOCAL_EXTERNAL_URL("本地外网地址",""),
    LOCAL_WEB_PORT("本地web端口",""),
    FILE_URL("土填地址",""),
    LOWCODE_WECOM_PROCESS_END("流程结束默认微信消息", "[\n" + "    {\n" +
            "      \"customValue\": \"您发起的流程已结束\",\n" + "      \"markdownType\": \"text\",\n" +
            "      \"valueType\": \"custom\"\n" + "    },\n" + "    {\n" + "      \"label\": \"表单名称\",\n" +
            "      \"fieldId\": \"formName\",\n" + "      \"fieldType\": \"system\",\n" +
            "      \"markdownType\": \"text\",\n" + "      \"valueType\": \"formValue\"\n" + "    },\n" + "    {\n" +
            "      \"label\": \"流程链接\",\n" + "      \"fieldId\": \"auditUrl\",\n" +
            "      \"fieldType\": \"system\",\n" + "      \"markdownType\": \"url\",\n" +
            "      \"valueType\": \"formValue\",\n" + "      \"urlLabel\": \"流程链接\"\n" + "    }\n" + "  ]"),
    LOWCODE_WECOM_TASK_START("流程开始默认微信消息", "[\n" + "    {\n" + "      \"customValue\": \"你有一个流程待处理\",\n" +
            "      \"markdownType\": \"text\",\n" + "      \"valueType\": \"custom\"\n" + "    },\n" + "    {\n" +
            "      \"label\": \"表单名称\",\n" + "      \"fieldId\": \"formName\",\n" +
            "      \"fieldType\": \"system\",\n" + "      \"markdownType\": \"text\",\n" +
            "      \"valueType\": \"formValue\"\n" + "    },\n" + "    {\n" + "      \"label\": \"任务名称\",\n" +
            "      \"fieldId\": \"taskName\",\n" + "      \"fieldType\": \"system\",\n" +
            "      \"markdownType\": \"text\",\n" + "      \"valueType\": \"formValue\"\n" + "    },\n" +
            "    {\n" + "      \"label\": \"发起人\",\n" + "      \"fieldId\": \"creator\",\n" +
            "      \"fieldType\": \"system\",\n" + "      \"markdownType\": \"text\",\n" +
            "      \"valueType\": \"formValue\"\n" + "    },\n" + "    {\n" + "      \"label\": \"处理链接\",\n" +
            "      \"fieldId\": \"auditUrl\",\n" + "      \"fieldType\": \"system\",\n" +
            "      \"markdownType\": \"url\",\n" + "      \"valueType\": \"formValue\",\n" +
            "      \"urlLabel\": \"审批链接\"\n" + "    }\n" + "  ]"),
    LOWCODE_WECOM_ORDER_SUCCESS( "订单成功默认微信消息", "[\n" + "\t{\n" +
            "\t\t\"customValue\": \"有一位用户下单\",\n" + "\t\t\"markdownType\": \"text\",\n" +
            "\t\t\"valueType\": \"custom\"\n" + "\t},\n" + "\t{\n" + "\t\t\"label\": \"公司\",\n" +
            "\t\t\"fieldId\": \"companyName\",\n" + "\t\t\"fieldType\": \"system\",\n" +
            "\t\t\"markdownType\": \"text\",\n" + "\t\t\"valueType\": \"formValue\"\n" + "\t},\n" + "\t{\n" +
            "\t\t\"label\": \"产品版本\",\n" + "\t\t\"fieldId\": \"itemName\",\n" + "\t\t\"fieldType\": \"system\",\n" +
            "\t\t\"markdownType\": \"text\",\n" + "\t\t\"valueType\": \"formValue\"\n" + "\t},\n" + "\t{\n" +
            "\t\t\"label\": \"价格\",\n" + "\t\t\"fieldId\": \"price\",\n" + "\t\t\"fieldType\": \"system\",\n" +
            "\t\t\"markdownType\": \"text\",\n" + "\t\t\"valueType\": \"formValue\"\n" + "\t},\n" + "\t{\n" +
            "\t\t\"label\": \"人数\",\n" + "\t\t\"fieldId\": \"itemNum\",\n" + "\t\t\"fieldType\": \"system\",\n" +
            "\t\t\"markdownType\": \"text\",\n" + "\t\t\"valueType\": \"formValue\"\n" + "\t},\n" + "\t{\n" +
            "\t\t\"label\": \"天数\",\n" + "\t\t\"fieldId\": \"orderPeriod\",\n" + "\t\t\"fieldType\": \"system\",\n" +
            "\t\t\"markdownType\": \"text\",\n" + "\t\t\"valueType\": \"formValue\"\n" + "\t},\n" + "\t{\n" +
            "\t\t\"label\": \"下单时间\",\n" + "\t\t\"fieldId\": \"createTime\",\n" +
            "\t\t\"fieldType\": \"system\",\n" + "\t\t\"markdownType\": \"text\",\n" +
            "\t\t\"valueType\": \"formValue\"\n" + "\t}\n" + "]"),
    LOWCODE_WECOM_ORDER_SUCCESS_URL( "订单成功默认发送地址", "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=915b7fbd-b374-4b67-a26a-f85b16d70dfa")

    ;

    private final String desc;

    private final String defaultValue;
}
