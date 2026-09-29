package com.wuji.admin.service;

import com.alibaba.fastjson.JSONObject;

public interface WeComThirdService extends WeComCommonService {
    void receive(String msg_signature, String timeStamp, String nonce, JSONObject json, String clientId);

     String getSuiteToken(String suiteId);

    String getThirdId(String phoneNumber);

    String getAccessTokenBySuitId(Long companyId, String suiteId);

}
