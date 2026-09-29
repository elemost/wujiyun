package com.wuji.message.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LarkMessageUtil {

    public static String getUrlContent(String url) {
        String format = " [Feishu Open Platform](%s)";
        return String.format(format, url);
    }

    public static String getAtContent(List<String> userIdList) {
        String format = "<at user_id=\"%s\"></at>";
        List<String> atList = new ArrayList<>();
        userIdList.forEach(userId -> {
            atList.add(String.format(format, userId));
        });
        return StringUtils.join(atList, " ");
    }

    public static List<JSONObject> getAtContentPost(List<String> userIdList) {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (String userId : userIdList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);
            jsonObject.put("tag", "at");
            jsonObjectList.add(jsonObject);
        }
        return jsonObjectList;
    }

    public static JSONObject getTextContentPost(String content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", content);
        jsonObject.put("tag", "text");
        return jsonObject;
    }

    public static JSONObject getAContentPost(String content, String href) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", content);
        jsonObject.put("href", href);
        jsonObject.put("tag", "a");
        return jsonObject;
    }

    public static JSONObject getImgContentPost(String imageKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("image_key", imageKey);
        jsonObject.put("tag", "img");
        return jsonObject;
    }

}
