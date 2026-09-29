package com.wuji.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
public class Md5Utils {
    public static String md5(String s) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(s.getBytes(StandardCharsets.UTF_8));
            // 将字节数组转换为 BigInteger（无符号）
            BigInteger bigInt = new BigInteger(1, algorithm.digest());

            // 转换为十六进制字符串
            return bigInt.toString(16);
        } catch (Exception e) {
            log.error("md5 error", e);
            return null;
        }
    }
}
