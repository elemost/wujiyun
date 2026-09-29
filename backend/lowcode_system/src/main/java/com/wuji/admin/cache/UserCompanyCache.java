package com.wuji.admin.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.ToolSpring;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class UserCompanyCache {
    private static final long maximumSize = 2000L;
    private static final long expireAfterWrite = 2L;
    private static final LoadingCache<String, UserVO> userCache =
            CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(expireAfterWrite, TimeUnit.MINUTES)
                    .build(new CacheLoader<String, UserVO>() {
                        private UserService userService;

                        @Override
                        public UserVO load(String s) {
                            if (userService == null) {
                                userService = ToolSpring.getBean(UserService.class);
                            }
                            String[] split = s.split("_");
                            return userService.getByCompanyAndUserId(Long.valueOf(split[0]), Long.valueOf(split[1]));
                        }
                    });
    ;


    private static UserVO getUser(String id) {
        try {
            return userCache.get(id);
        } catch (Exception e) {
            log.error("get value error from cache by key[" + id + "]", e);
            return null;
        }
    }

    /**
     * 通过id获取用户
     *
     * @param id
     * @return
     */
    public static UserVO getUserById(String id) {
        UserVO user = getUser(id);
        if (user == null) {
            user = getUser(id);
        }
        return user;
    }
}
