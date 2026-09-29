package com.wuji.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class AESUtils {

    private static final String ALGORITHM = "AES";
    public static final String KEY = "scanqrcode202408";


    //HZWJ ascii 码
    private static final byte[] HZWJ_ASCII = {72, 90, 87, 85, 74, 73, 65};
    private static final int headLen;

    static {
        headLen = HZWJ_ASCII.length + 1 + 1;
    }

    private static final byte version = 0x00;

    public static String encrypt(String password) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedPassword = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encryptedPassword);
        } catch (Exception e) {
            log.error("加密失败", e);
        }
        return "";
    }


    public static String encrypt(String password, String value) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedPassword = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encryptedPassword);
        } catch (Exception e) {
            log.error("加密失败", e);
        }
        return "";
    }

    public static String decrypt(String value) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedPassword = Base64.getDecoder().decode(value);
            byte[] decryptedPassword = cipher.doFinal(decodedPassword);
            return new String(decryptedPassword);
        } catch (Exception e) {
            log.error("解密失败", e);
        }
        return null;
    }

    public static String decrypt(byte[] password, String decryptMessage) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(password, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedPassword = Base64.getDecoder().decode(decryptMessage);
            byte[] decryptedPassword = cipher.doFinal(decodedPassword);
            return new String(decryptedPassword);
        } catch (Exception e) {
            log.error("解密失败", e);
        }
        return null;
    }

    public static Object encryptData(Object data, String encryptedPassword) {
        if (data == null) {
            return null;
        }
        byte[] fileData = data.toString().getBytes(StandardCharsets.UTF_8);
        byte[] entryKey = encryptedPassword.getBytes(StandardCharsets.UTF_8);
        try {
            // 计算原文件md5的值
            MessageDigest md = MessageDigest.getInstance("MD5");
            SecretKeySpec key = new SecretKeySpec(entryKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] entryFile = cipher.doFinal(fileData);
            byte[] md5Value = md.digest(entryFile);
            byte[] result = new byte[headLen + md5Value.length + entryFile.length];
            System.arraycopy(HZWJ_ASCII, 0, result, 0, HZWJ_ASCII.length);
            result[HZWJ_ASCII.length] = version;
            result[headLen - 1] = (byte) (md5Value.length & 0xff);
            System.arraycopy(md5Value, 0, result, headLen, md5Value.length);
            System.arraycopy(entryFile, 0, result, headLen + md5Value.length, entryFile.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException("加密失败");
        }
    }

    public static Object decryData(Object data, String encryptedPassword) {
        if (data == null) {
            return null;
        }
        if (data.toString().getBytes().length < 2) {
            return data;
        }
        byte[] entryFile;
        try {
            entryFile = Base64.getDecoder().decode(data.toString());
        } catch (Exception e) {
            return data;
        }
        byte[] decryKey = encryptedPassword.getBytes();
        if (!isEntryFile(entryFile)) {
            return data;
        }
        byte len = entryFile[HZWJ_ASCII.length + 1];
        byte[] file = new byte[entryFile.length - len - headLen];
        System.arraycopy(entryFile, headLen + len, file, 0, file.length);
        try {
            SecretKeySpec key = new SecretKeySpec(decryKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(file);
            return new String(result);
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException |
                 NoSuchPaddingException e) {
            throw new RuntimeException("解密失败");
        }
    }

    public static boolean isEntryFile(byte[] fileData) {
        if (fileData.length < headLen) {
            return false;
        }
        byte[] hzwj = new byte[HZWJ_ASCII.length];
        System.arraycopy(fileData, 0, hzwj, 0, HZWJ_ASCII.length);
        if (!Arrays.equals(hzwj, HZWJ_ASCII)) {
            return false;
        }
        byte version = fileData[HZWJ_ASCII.length];
        int len = fileData[headLen - 1] & 0xff;
        if (version == 0x00) {
            if (fileData.length < (headLen + len)) {
                return false;
            }
            byte[] md5 = new byte[len];
            byte[] file = new byte[fileData.length - len - headLen];
            System.arraycopy(fileData, headLen, md5, 0, md5.length);
            System.arraycopy(fileData, headLen + len, file, 0, file.length);
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] md5Value = md.digest(file);
                return Arrays.equals(md5Value, md5);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("md5 组件不存在");
            }
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        Object s = AESUtils.encryptData("测啊说", AESUtils.KEY);
        Object s1 = AESUtils.decryData(s, AESUtils.KEY);
        String encrypt = encrypt("wujicloudkey2025", "wsm.005102");
        log.info(encrypt);
    }
}
