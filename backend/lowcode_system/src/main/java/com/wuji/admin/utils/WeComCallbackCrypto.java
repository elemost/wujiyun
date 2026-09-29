package com.wuji.admin.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WeComCallbackCrypto {
    /**
     * 生成企业微信 JS-SDK 签名
     *
     * @param jsapiTicket 企业微信获取的 jsapi_ticket
     * @param noncestr    随机字符串
     * @param timestamp   时间戳（秒级）
     * @param url         当前网页的 URL（不包含 # 及后面部分）
     * @return 签名结果（signature）
     */
    public static String generateSignature(String jsapiTicket, String noncestr, long timestamp, String url) {
        // 1. 组装参数（按 ASCII 排序）
        Map<String, String> params = new HashMap<>();
        params.put("jsapi_ticket", jsapiTicket);
        params.put("noncestr", noncestr);
        params.put("timestamp", String.valueOf(timestamp));
        params.put("url", url);

        // 2. 按 key 排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 3. 拼接成 key=value&key=value 格式
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String value = params.get(key);
            if (i > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(value);
        }
        String string1 = sb.toString();

        // 4. SHA1 加密
        return sha1(string1);
    }

    /**
     * SHA1 加密工具方法
     *
     * @param str 待加密字符串
     * @return 加密后的十六进制字符串
     */
    private static String sha1(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte[] messageDigest = digest.digest();

            // 转为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

}
