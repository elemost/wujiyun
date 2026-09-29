package com.wuji.plugin.service.impl.plugin;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.ContentTypeEnum;
import com.wuji.common.enums.MethodEnum;
import com.wuji.common.model.request.ApiRequest;
import com.wuji.common.utils.HttpUtils;
import com.wuji.plugin.model.info.config.WeComRobotConfig;
import com.wuji.plugin.service.PluginUseService;
import org.springframework.stereotype.Service;

@Service
public class WeComRobotPluginImpl implements PluginUseService {

    @Override
    public String pluginType() {
        return "WECOM_ROBOT";
    }

    @Override
    public Object execute(Object object) {
        WeComRobotConfig weComRobotConfig =
                JSONObject.parseObject(JSONObject.toJSONString(object), WeComRobotConfig.class);
        ApiRequest apiRequest = getApiRequest(weComRobotConfig);
        HttpUtils.apiRequest(apiRequest);
        return null;
    }

    private static ApiRequest getApiRequest(WeComRobotConfig weComRobotConfig) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod(MethodEnum.POST);
        apiRequest.setUrl(weComRobotConfig.getUrl());
        apiRequest.setContentTypeEnum(ContentTypeEnum.JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", "markdown");
        JSONObject markContent = new JSONObject();
        markContent.put("content", weComRobotConfig.getMarkdowns());
        jsonObject.put("markdown", markContent);
        apiRequest.setRequestJsonBody(jsonObject);
        return apiRequest;
    }
}
