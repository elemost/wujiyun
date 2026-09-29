package com.wuji.service.utils;

import com.alibaba.ttl.TransmittableThreadLocal;

public class DataStreamExecuteUtils {
    private static final TransmittableThreadLocal<Boolean> needNewThreadContext = new TransmittableThreadLocal<>();

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public static Boolean needNewThread() {
        Boolean needNewThread = needNewThreadContext.get();
        if (needNewThread == null) {
            return true;
        }
        return needNewThread;
    }

    public static void setNeedNewThread(Boolean needNewThread) {
        needNewThreadContext.set(needNewThread);
    }

    public static void removeNeedNewThread() {
        needNewThreadContext.remove();
    }
}
