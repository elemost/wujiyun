package com.wuji.plugin.service.impl.plugin;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.ContentTypeEnum;
import com.wuji.common.enums.MethodEnum;
import com.wuji.common.model.request.ApiRequest;
import com.wuji.common.utils.HttpUtils;
import com.wuji.plugin.service.PluginUseService;
import org.springframework.stereotype.Service;

@Service
public class IdCardCheckPluginImpl implements PluginUseService {
    @Override
    public String pluginType() {
        return "ID_CARD_CHECK";
    }

    @Override
    public Object execute(Object object) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(object));
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setRequestJsonBody(jsonObject);
        apiRequest.setUrl("https://kzidcardv1.market.alicloudapi.com/api-mall/api/id_card/check");
        apiRequest.setMethod(MethodEnum.POST);
        apiRequest.putHeader("Authorization", "APPCODE " + "e2cfdc84f7eb4847a64305f43914f8f9");
        apiRequest.setContentTypeEnum(ContentTypeEnum.FORM_URLENCODED);
        return JSONObject.parseObject(HttpUtils.apiRequest(apiRequest));
    }
}
