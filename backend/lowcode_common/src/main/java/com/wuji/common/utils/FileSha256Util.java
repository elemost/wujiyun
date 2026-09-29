package com.wuji.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class FileSha256Util {

    public static void main(String[] args) {
        try {
            File dockerCompose = new File("/Users/huangzeman/hzmwork/wuji/lowcode/docker/docker-compose.yml");
            String dockerComposeSha256 = sha256File(dockerCompose);
            log.info("docker-compose.yml文件: {}", dockerComposeSha256);

            File init = new File("/Users/huangzeman/hzmwork/wuji/lowcode/docker/install/debian/init.sh");
            String initSha256 = sha256File(init);
            log.info("init.sh文件: {}", initSha256);
        } catch (Exception e) {
            log.error("hash文件失败", e);
        }
    }

    /**
     * 计算文件SHA256 hex摘要
     *
     * @param file 文件
     * @return 小写sha256字符串（同sha256sum输出）
     * @throws IOException              文件读取异常
     * @throws NoSuchAlgorithmException 算法不存在
     */
    public static String sha256File(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // 流式读取，支持大文件，不会一次性加载全部到内存
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
        }
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    /**
     * byte数组转十六进制小写字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
