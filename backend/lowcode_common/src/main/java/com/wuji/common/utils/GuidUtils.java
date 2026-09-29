package com.wuji.common.utils;

import java.util.UUID;

/**
 * 生成UUID
 *
 * @author Jackie
 * @date 2022-11-09
 */
public class GuidUtils {

    private GuidUtils() {
    }

    /**
     * 获取guid
     *
     * @return
     */
    public static String getGuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }


    /**
     * 随机生成6位数
     *
     * @return
     */
    public static String getRandNum() {
        StringBuilder stringBuffer = new StringBuilder();
        int num;
        for (int i = 0; i < 6; i++) {
            num = (int) (Math.random() * 9 + 1);
            stringBuffer.append(num);
        }
        return stringBuffer.toString();
    }


}
