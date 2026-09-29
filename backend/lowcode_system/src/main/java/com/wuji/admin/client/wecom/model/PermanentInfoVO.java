package com.wuji.admin.client.wecom.model;

import com.wuji.admin.client.wecom.WeComResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class PermanentInfoVO extends WeComResult {

    private String access_token;

    private Integer expires_in;

    private String permanent_code;

    private Corp auth_corp_info;

    private Map<String, String> auth_user_info;

    @Data
    public static class Corp {
        private String corpid;

        private String corp_name;
    }

    @Data
    public static class user {
        private String userid;

        private String open_userid;

        private String name;
    }
}
