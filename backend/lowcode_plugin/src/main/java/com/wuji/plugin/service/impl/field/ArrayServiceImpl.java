package com.wuji.plugin.service.impl.field;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.utils.GuidUtils;
import com.wuji.plugin.model.info.PluginParamMapping;
import com.wuji.plugin.model.info.PluginReturnMapping;
import com.wuji.plugin.service.HttpResultService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArrayServiceImpl extends PluginCommonServiceImpl implements HttpResultService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.SUB_FORM_TYPE.getFieldType();
    }

    @Override
    public void buildHttpResponse(String result, PluginReturnMapping pluginReturnMapping, JSONObject resultJson,
                                  String parentJsonPath) {
        List<PluginReturnMapping> children = pluginReturnMapping.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        Object read = JSONPath.read(result, parentJsonPath);
        JSONArray jsonArray = JSONArray.parseArray(JSONObject.toJSONString(read));
        JSONArray returnJsonArray = new JSONArray();
        if (read != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", GuidUtils.getGuid());
                for (PluginReturnMapping child : children) {
                    String jsonpath = parentJsonPath + "." + child.getJsonPath() + "[" + i + "]";
                    whileBuildHttpResponse(result, child, jsonObject, jsonpath);
                }
                returnJsonArray.add(jsonObject);
            }
            resultJson.put(pluginReturnMapping.getFieldId(), returnJsonArray);
        }
    }


    @Override
    public void buildRequest(JSONObject resultJson, PluginParamMapping pluginParamMapping) {
        List<List<PluginParamMapping>> subFormList = pluginParamMapping.getSubFormList();
        JSONArray subFormArray = new JSONArray();
        for (List<PluginParamMapping> subForm : subFormList) {
            JSONObject index = new JSONObject();
            for (PluginParamMapping subFormIndex : subForm) {
                whileBuildRequest(index, subFormIndex);
            }
            subFormArray.add(index);
        }
        resultJson.put(pluginParamMapping.getFieldId(), subFormArray);
    }
}
