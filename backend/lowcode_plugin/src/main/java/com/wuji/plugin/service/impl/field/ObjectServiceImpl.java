package com.wuji.plugin.service.impl.field;

import com.alibaba.fastjson.JSONObject;
import com.wuji.plugin.model.info.PluginReturnMapping;
import com.wuji.plugin.service.HttpResultService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObjectServiceImpl extends PluginCommonServiceImpl implements HttpResultService {

    @Override
    public String fieldType() {
        return "object";
    }

    @Override
    public void buildHttpResponse(String result, PluginReturnMapping pluginReturnMapping, JSONObject resultJson,
                                  String parentJsonPath) {
        List<PluginReturnMapping> children = pluginReturnMapping.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            for (PluginReturnMapping child : children) {
                whileBuildHttpResponse(result, child, resultJson, pluginReturnMapping.getJsonPath());
            }
        }
    }
}
