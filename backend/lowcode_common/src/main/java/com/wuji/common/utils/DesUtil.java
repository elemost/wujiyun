package com.wuji.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;

@Slf4j
public class DesUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /*** DES 加密 加密填充模式 DES/ECB/PKCS7Padding * @param datasource byte[] 要加密的 byte 数组 * @param password String 私钥（8 的倍数） * @return byte[] */
    public static byte[] encrypt(byte[] datasource, String password) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            // 创建一个密匙工厂，然后用它把 DESKeySpec 转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher 对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding");
            // 用密匙初始化 Cipher 对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(addZero(datasource));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /*** DES 解密 加密填充模式 DES/ECB/PKCS7Padding * @param src byte[] 要解密的数组 * @param password String 私钥（8 的倍数） * @return byte[] * @throws Exception */
    public static byte[] decrypt(byte[] src, String password) {
        try {
            // DES 算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            // 创建一个 DESKeySpec 对象
            DESKeySpec desKey = new DESKeySpec((password.getBytes()));
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 将 DESKeySpec 对象转换成 SecretKey 对象
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher 对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding");
            // 用密匙初始化 Cipher 对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            // 真正开始解密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
           log.error("解密失败", e);
        }
       return null;
    }

    public static byte[] getKey(byte[] keyRule) {
        SecretKeySpec key = null;
        byte[] keyByte = keyRule;
        System.out.println(keyByte.length);
        // 创建一个空的八位数组,默认情况下为 0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        int i = 0;
        for (; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        key = new SecretKeySpec(byteTemp, "DES");
        return key.getEncoded();
    }

    public static byte[] addZero(byte[] data) {
        byte[] dataByte = data;
        if (data.length % 8 != 0) {
            byte[] temp = new byte[8 - data.length % 8];
            dataByte = byteMerger(data, temp);
        }
        return dataByte;

    }

    // java 合并两个 byte 数组 // System.arraycopy()方法
    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /*** byte 数组转 hex * @param bytes * @return */
    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
            // 每个字节由两个字符 表示，位数不够，高位补 0
        }
        return sb.toString().trim();
    }



}