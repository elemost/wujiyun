package com.wuji.systemapi.utils;

import com.wuji.common.utils.AESUtils;

public class DataEncryptUtils {

    public static String encrypt(String password, String value) {
        return AESUtils.encrypt(password, value);
    }

    public static String decrypt(String password, String value) {
        return AESUtils.decrypt(password.getBytes(), value);
    }
}
