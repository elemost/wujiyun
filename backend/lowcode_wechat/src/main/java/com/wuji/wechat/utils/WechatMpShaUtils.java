package com.wuji.wechat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WechatMpShaUtils {

    public static String encrypt(Long timestamp, String jsapi_ticket, String url, String noncestr) {

        String format = "jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s";
        String str = String.format(format, jsapi_ticket, noncestr, timestamp, url);
        try {
            return getSHA1(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSHA1(String input) throws NoSuchAlgorithmException {
        // 获取MessageDigest实例
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        // 将输入字符串转换为字节数组
        byte[] messageDigest = md.digest(input.getBytes());

        // 将字节数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
