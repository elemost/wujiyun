package com.wuji.common.utils;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.wuji.common.model.domain.UserDomain;

public class UserUtils {
    private static final TransmittableThreadLocal<UserDomain> userContext = new TransmittableThreadLocal<>();

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public static UserDomain getUser() {
        return userContext.get();
    }

    public static void setUser(UserDomain userDomain) {
        userContext.set(userDomain);
    }

    public static void clearUser() {
        userContext.remove();
    }

    public static void clearCurrentUserInfo() {

    }
}
