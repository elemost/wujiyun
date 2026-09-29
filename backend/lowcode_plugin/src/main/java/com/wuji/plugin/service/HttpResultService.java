package com.wuji.plugin.service;

import com.alibaba.fastjson.JSONObject;
import com.wuji.plugin.model.info.PluginParamMapping;
import com.wuji.plugin.model.info.PluginReturnMapping;

public interface HttpResultService {
    String fieldType();

    void buildHttpResponse(String result, PluginReturnMapping pluginReturnMapping, JSONObject resultJson,
                           String parentJsonPath);

    void buildRequest(JSONObject resultJson, PluginParamMapping pluginParamMapping);
}
