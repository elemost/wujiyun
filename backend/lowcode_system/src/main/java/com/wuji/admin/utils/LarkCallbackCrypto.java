package com.wuji.admin.utils;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LarkCallbackCrypto {
    public static Boolean calculateSignature(String timestamp, String nonce, String encryptKey, String bodyString,
                                             String signature) {
        try {
            String content = timestamp + nonce + encryptKey + bodyString;
            MessageDigest alg = MessageDigest.getInstance("SHA-256");
            String sign = Hex.encodeHexString(alg.digest(content.getBytes()));
            return sign.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

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
