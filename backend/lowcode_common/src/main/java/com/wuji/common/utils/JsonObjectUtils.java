package com.wuji.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonObjectUtils {
    public static JSONArray getJsonArray(JSONObject jsonObject, String fieldId) {
        JSONArray jsonArray = new JSONArray();
        if (jsonObject.get(fieldId) == null) {
            jsonArray = new JSONArray();
        } else {
            jsonArray = jsonObject.getJSONArray(fieldId);
        }
        return jsonArray;
    }
}
