package com.wuji.message.utils;

import com.alibaba.fastjson.JSONObject;

public class DingTalkMessageUtil {
    public static JSONObject getMarkDown(String title, String text) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("text", text);
        return jsonObject;
    }

    public static String getUrlContent(String content, String url) {
        String format = "[%s](%s)";
        return String.format(format, content, url);
    }
}
