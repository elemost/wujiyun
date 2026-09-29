package com.wuji.admin.client.wecom.model;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.client.wecom.WeComResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthInfoVO extends WeComResult {

    private AuthInfo auth_info;

    private JSONObject auth_corp_info;

    private JSONObject dealer_corp_info;

    @Data
    public static class Agent {
        private Integer agentid;

        private String name;

        private JSONObject privilege;

        private JSONObject shared_from;
    }

    @Data
    public static class AuthInfo {
        private List<Agent> agent;
    }
}
