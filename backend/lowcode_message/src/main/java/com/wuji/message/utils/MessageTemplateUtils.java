package com.wuji.message.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTemplateUtils {
    public static String dealMessage(String content, Map<String, Object> param) {
        String regexFormat = "\\$\\{([\\w.]+)\\}";
        Pattern pattern = Pattern.compile(regexFormat);
        Matcher matcher = pattern.matcher(content);
        List<String> matchList = new ArrayList<>();
        if (matcher.find()) {
            // 提取第一个分组中的内容
            String result = matcher.group(1);
            matchList.add(result);
        }
        for (String match : matchList) {
            content = content.replaceAll("\\$\\{" + match + "\\}", param.getOrDefault(match, " ").toString());
        }
        return content;
    }
}
