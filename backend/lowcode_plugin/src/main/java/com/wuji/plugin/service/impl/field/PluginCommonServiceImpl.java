package com.wuji.plugin.service.impl.field;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.wuji.plugin.context.HttpResultContext;
import com.wuji.plugin.model.info.PluginParamMapping;
import com.wuji.plugin.model.info.PluginReturnMapping;
import com.wuji.plugin.service.HttpResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginCommonServiceImpl implements HttpResultService {

    @Autowired
    private HttpResultContext httpResultContext;

    @Override
    public String fieldType() {
        return null;
    }

    @Override
    public void buildHttpResponse(String result, PluginReturnMapping pluginReturnMapping, JSONObject resultJson,
                                  String parentJsonPath) {
        resultJson.put(pluginReturnMapping.getFieldId(), JsonPath.read(result, pluginReturnMapping.getJsonPath()));
    }

    @Override
    public void buildRequest(JSONObject resultJson, PluginParamMapping pluginParamMapping) {
        resultJson.put(pluginParamMapping.getFieldId(), pluginParamMapping.getValue());
    }

    public void whileBuildHttpResponse(String result, PluginReturnMapping pluginReturnMapping, JSONObject resultJson,
                                       String parentJsonPath) {
        HttpResultService httpResultService = httpResultContext.getHandler(pluginReturnMapping.getFieldType());
        if (httpResultService == null) {
            resultJson.put(pluginReturnMapping.getFieldId(),
                    JsonPath.read(result, parentJsonPath + "." + pluginReturnMapping.getJsonPath()));
        } else {
            httpResultService.buildHttpResponse(result, pluginReturnMapping, resultJson, parentJsonPath);
        }
    }

    public void whileBuildRequest(JSONObject resultJson, PluginParamMapping pluginParamMapping) {
        HttpResultService httpResultService = httpResultContext.getHandler(pluginParamMapping.getFieldType());
        if (httpResultService != null) {
            httpResultService.buildRequest(resultJson, pluginParamMapping);
        } else {
            resultJson.put(pluginParamMapping.getFieldId(), pluginParamMapping.getValue());
        }
    }
}
