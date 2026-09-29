package com.wuji.service.utils;

import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.UserUtils;

import java.util.Base64;

public class MongoDataUtils {

    public static Object decryptAndEncryptReturn(Object data) {
        if (data == null) {
            return null;
        }
        Object decryData = AESUtils.decryData(data, UserUtils.getUser().getCompanyUuid());
        byte[] decode;
        try {
            decode = Base64.getDecoder().decode(data.toString());
        } catch (Exception e) {
            return data;
        }
        if (AESUtils.isEntryFile(decode)) {
            return AESUtils.encryptData(decryData, AESUtils.KEY);
        } else {
            return data;
        }
    }

    public static Object decryptAndEncryptInsert(Object data) {
        if (data == null) {
            return null;
        }
        Object decryData = AESUtils.decryData(data, AESUtils.KEY);
        byte[] decode;
        try {
            decode = Base64.getDecoder().decode(data.toString());
        } catch (Exception e) {
            return data;
        }
        if (AESUtils.isEntryFile(decode)) {
            return AESUtils.encryptData(decryData, UserUtils.getUser().getCompanyUuid());
        } else {
            return data;
        }
    }
}
