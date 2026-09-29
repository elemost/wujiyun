package com.wuji.framework.security.service;

import com.wuji.common.constant.CacheConstants;
import com.wuji.common.model.vo.UserVO;
import com.wuji.framework.exception.FrameworkException;
import com.wuji.framework.security.context.AuthenticationContextHolder;
import com.wuji.common.utils.PasswordUtil;
import com.wuji.common.utils.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 登录密码方法
 *
 * @author hzm
 */
@Component
public class SysPasswordService {
    @Autowired
    private RedisCache redisCache;

    // @Value(value = "${user.password.maxRetryCount}")
    // private int maxRetryCount;
    //
    // @Value(value = "${user.password.lockTime}")
    // private int lockTime;

    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return CacheConstants.PWD_ERR_CNT_KEY + username;
    }

    public void validate(UserVO user) {
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
        String username = usernamePasswordAuthenticationToken.getName();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString();
        if (!matches(user, password)) {

            throw new FrameworkException("账号密码错误");
        } else {
            clearLoginRecordCache(username);
        }
    }

    public boolean matches(UserVO user, String rawPassword) {
        return PasswordUtil.matchesPassword(rawPassword, user.getPassword());
    }

    public void clearLoginRecordCache(String loginName) {
        if (redisCache.hasKey(getCacheKey(loginName))) {
            redisCache.deleteObject(getCacheKey(loginName));
        }
    }
}
