package com.wuji.admin.service;

import com.alibaba.fastjson.JSONObject;

public interface DingTalkThirdService extends OrganizePullDataService {
    void receive(String msg_signature, String timeStamp, String nonce, JSONObject json, String clientId);

     String getSuiteToken();
}
