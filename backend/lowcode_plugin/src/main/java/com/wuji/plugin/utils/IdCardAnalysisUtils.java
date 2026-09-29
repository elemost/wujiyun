package com.wuji.plugin.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdCardAnalysisUtils {
    /**
     * 解析连续字符串格式的身份证OCR结果
     *
     * @param ocrStr 连续字符串
     * @return 提取后的信息Map
     */
    public static JSONObject parseIdCard(String ocrStr) {
        JSONObject result = new JSONObject();
        String remaining = ocrStr; // 剩余未解析的字符串
        // 定义关键字顺序（按身份证字段出现顺序）
        List<List<String>> keywords =
                Lists.newArrayList(Lists.newArrayList("姓名"), Lists.newArrayList("性别"), Lists.newArrayList("民族"),
                        Lists.newArrayList("出生"), Lists.newArrayList("住址"), Lists.newArrayList("公民身份号码"));
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("姓名", "name");
        keyMap.put("性别", "sex");
        keyMap.put("民族", "nation");
        keyMap.put("出生", "birth");
        keyMap.put("住址", "address");
        keyMap.put("公民身份号码", "idCard");
        for (int i = 0; i < keywords.size(); i++) {
            JSONObject keyResult = getKeyResult(keywords.get(i), remaining);
            String keyword = keyResult.getString("keyword");
            int keyIndex = keyResult.getIntValue("keyIndex");
            if (keyIndex == -1) {
                continue;
            }
            // 截取关键字后的内容
            String contentAfterKey = remaining.substring(keyIndex + keyword.length());
            // 确定当前字段的结束位置（下一个关键字的位置）
            int endIndex = -1;
            if (i < keywords.size() - 1) {
                // 不是最后一个关键字，找下一个关键字的位置
                JSONObject nextResult = getKeyResult(keywords.get(i + 1), remaining);
                String nextKeyword = nextResult.getString("keyword");
                endIndex = contentAfterKey.indexOf(nextKeyword);
            }
            // 提取当前字段的值
            String value;
            if (endIndex == -1) {
                // 最后一个字段或未找到下一个关键字，取剩余全部内容
                value = contentAfterKey;
            } else {
                // 取到下一个关键字之前的内容
                value = contentAfterKey.substring(0, endIndex);
            }
            // 存储结果
            result.put(keyMap.get(keyword), value);
            // 更新剩余字符串（从当前字段结束位置开始）
            if (endIndex != -1) {
                remaining = contentAfterKey.substring(endIndex);
            } else {
                // 已到最后一个字段，无需继续解析
                break;
            }
        }
        return result;
    }

    private static JSONObject getKeyResult(List<String> keywordList, String remaining) {
        String keyword = "";
        int keyIndex = -1;
        for (String keywordIndex : keywordList) {
            keyIndex = remaining.indexOf(keywordIndex);
            if (keyIndex != -1) {
                keyword = keywordIndex;
                break;
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keyword", keyword);
        jsonObject.put("keyIndex", keyIndex);
        return jsonObject;
    }

    public static void main(String[] args) {
        // OCR返回的连续字符串
        String ocrResult =
                "姓名黄泽慢性别男民族汉出生1998年4月3日住址浙江省瑞安市桐浦镇岭南村公民身份号码330381199804036415";

        // 解析结果
        JSONObject idCardInfo = parseIdCard(ocrResult);

        // 打印结果
        System.out.println("身份证信息提取结果：" + idCardInfo);

    }
}
