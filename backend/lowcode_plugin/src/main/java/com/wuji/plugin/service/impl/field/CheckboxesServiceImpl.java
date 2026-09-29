package com.wuji.plugin.service.impl.field;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.plugin.model.info.PluginReturnMapping;
import com.wuji.plugin.service.HttpResultService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckboxesServiceImpl extends PluginCommonServiceImpl implements HttpResultService {
    @Override
    public String fieldType() {
        return FormFieldTypeEnum.CHECKBOXES.getFieldType();
    }

    @Override
    public void buildHttpResponse(String result, PluginReturnMapping pluginReturnMapping, JSONObject resultJson,
                                  String parentJsonPath) {
        String jsonPath = parentJsonPath + "." + pluginReturnMapping.getJsonPath();
        Object read = JSONPath.read(result, jsonPath);
        try {
            List<String> jsonList = JSONArray.parseArray(JSONObject.toJSONString(read), String.class);
            resultJson.put(pluginReturnMapping.getFieldId(), jsonList);
        } catch (Exception e) {
            resultJson.put(pluginReturnMapping.getFieldId(), null);
        }
    }
}
