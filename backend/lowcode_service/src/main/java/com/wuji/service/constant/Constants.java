package com.wuji.service.constant;

import com.wuji.common.model.info.DingTalkConfig;

import java.net.URLEncoder;

public class Constants {
    public static final String VARIABLE_FORM_ID = "_formId";

    public static final String VARIABLE_CREATE_FORM_ID = "_createFormId";

    public static final String VARIABLE_FORM_VERSION = "_formVersion";

    public static final String VARIABLE_DATA_UUID = "_dataUuid";

    public static final String VARIABLE_APPLICATION_ID = "_applicationId";

    public static final String VARIABLE_PARENT_TASK_ID = "_taskParentId";

    public static final String VARIABLE_PARENT_PROCESS_INSTANCE_ID = "_processInstanceParentId";

    public static final String VARIABLE_FORM_SERVICE = "_formProcessInstanceInterfaceImpl";

    public static final String UUID = "uuid";

    public static final String PROCESS_INSTANCE_ID = "processInstanceId";

    /**
     * 子表单类型
     */
    public static final String SUB_FORM_TYPE = "input-table-wuji";

    public static final String FORM_DATE_TYPE = "input-date-wuji";

    public static final String AGGREGATE_TABLE = "AGG_";

    public static final String FAC_PREFIX = "FAC_";

    public static final Integer MAX_TRIGGER_CYCLE = 5;

    public static final String SUPER_MANAGE = "super_manger";

    public static final String ADMIN_PRIVILEGE = "admin_privilege";

    public static final String SIZE = "size";

    public static String getDingTalkUrl(String domainName, DingTalkConfig dingTalkConfig, String lowcodeUrl) {
        String dingLogin = getDingTalkSSoUrl(domainName, dingTalkConfig);
        return "dingtalk://dingtalkclient/page/link?url=" +
                URLEncoder.encode(dingLogin + URLEncoder.encode(lowcodeUrl)) + "&popup_wnd=true&width=800&height=1000";
    }

    public static String getDingTalkSSoUrl(String domainName, DingTalkConfig dingTalkConfig) {
        return String.format(domainName + "smallProgram/dingLogin?corpId=%s&agentId=%s&secretId=%s&redirect=",
                dingTalkConfig.getCorpId(), dingTalkConfig.getAgentId(), dingTalkConfig.getClientId());
    }

    public static String mcpToken(String token) {
        return "mcpToken_" + token;
    }

    public static String removeMcpToken(String token) {
        return token.replace("mcpToken_", "");
    }
}
