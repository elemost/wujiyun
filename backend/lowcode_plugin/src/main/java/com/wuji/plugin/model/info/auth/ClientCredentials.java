package com.wuji.plugin.model.info.auth;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.ContentTypeEnum;
import com.wuji.common.enums.MethodEnum;
import com.wuji.common.model.request.ApiRequest;
import com.wuji.plugin.model.info.HttpIdentityAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClientCredentials extends HttpIdentityAuth {
    private String clientId;

    private String clientSecret;

    private String tokenUrl;

    private JSONObject authParams;

    private JSONObject headerJson;

    private String tokenJsonPath;

    private Integer expireTime;

    public ApiRequest getApiRequest() {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod(MethodEnum.POST);
        apiRequest.setUrl(tokenUrl);
        JSONObject requestJson = new JSONObject();
        requestJson.put("client_id", clientId);
        requestJson.put("client_secret", clientSecret);
        if (authParams != null) {
            requestJson.putAll(authParams);
        }
        apiRequest.setQueryJson(requestJson);
        apiRequest.setContentTypeEnum(ContentTypeEnum.FORM_URLENCODED);
        apiRequest.setHeaderJson(headerJson);
        apiRequest.setJsonpath(tokenJsonPath);
        return apiRequest;
    }
}
