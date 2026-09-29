package com.wuji.plugin.service.impl.field;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.plugin.model.info.PluginReturnMapping;
import com.wuji.plugin.service.HttpResultService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class NumberServiceImpl extends PluginCommonServiceImpl implements HttpResultService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.INPUT_NUMBER.getFieldType();
    }

    @Override
    public void whileBuildHttpResponse(String result, PluginReturnMapping pluginReturnMapping,
                                       JSONObject resultJson, String parentJsonPath) {
        String jsonPath = parentJsonPath + "." + pluginReturnMapping.getJsonPath();
        Object number = JSONPath.read(result, jsonPath);
        if (number == null) {
            return;
        }
        String numberString = number.toString();
        String fieldId = pluginReturnMapping.getFieldId();
        if (Objects.equals(numberString, "")) {
            resultJson.put(fieldId, null);
        } else if (!NumberUtils.isCreatable(numberString)) {
            resultJson.put(fieldId, null);
        } else {
            if (number instanceof Integer) {
                resultJson.put(fieldId, Integer.valueOf(numberString));
            } else if (number instanceof Long) {
                resultJson.put(fieldId, Long.valueOf(numberString));
            } else {
                resultJson.put(fieldId, Double.valueOf(numberString));
            }
        }
    }
}
