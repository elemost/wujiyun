package com.wuji.admin.utils;

import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Decrypt {
    private byte[] keyBs;

    public Decrypt(String key) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AdminException(AdminResultCode.DECRYPT);
        }
        keyBs = digest.digest(key.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(String base64) {
        try {
            byte[] decode = Base64.getDecoder().decode(base64);
            Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
            byte[] iv = new byte[16];
            System.arraycopy(decode, 0, iv, 0, 16);
            byte[] data = new byte[decode.length - 16];
            System.arraycopy(decode, 16, data, 0, data.length);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBs, "AES"), new IvParameterSpec(iv));
            byte[] r = cipher.doFinal(data);
            if (r.length > 0) {
                int p = r.length - 1;
                for (; p >= 0 && r[p] <= 16; p--) {
                }
                if (p != r.length - 1) {
                    byte[] rr = new byte[p + 1];
                    System.arraycopy(r, 0, rr, 0, p + 1);
                    r = rr;
                }
            }
            return new String(r, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }

    }
}
