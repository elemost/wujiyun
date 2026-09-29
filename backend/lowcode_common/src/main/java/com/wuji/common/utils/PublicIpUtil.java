package com.wuji.common.utils;


public class PublicIpUtil {
    public static String getPublicIp() {
        return System.getenv("PUBLIC_IP");
    }

    public static String getPublicIpPort() {
        return System.getenv("HOST_WEB_PORT");
    }

    public static String getInitUuid() {
        return System.getenv("INIT_UUID");
    }


}
