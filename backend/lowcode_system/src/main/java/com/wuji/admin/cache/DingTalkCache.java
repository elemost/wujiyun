package com.wuji.admin.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.handler.PullDataContext;
import com.wuji.common.utils.ToolSpring;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class DingTalkCache {
    private static final LoadingCache<Long, String> tokenCache;

    static {
        tokenCache =
                CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.HOURS).build(new CacheLoader<Long, String>() {

                    private PullDataContext pullDataContext;

                    private synchronized String loadValue(Long key) {
                        if (pullDataContext == null) {
                            pullDataContext = ToolSpring.getBean(PullDataContext.class);
                        }
                        return pullDataContext.getHandler(CompanyDataSourceEnum.DING_TALK.name())
                                .getAccessToken(key);
                    }

                    @Override
                    public String load(Long key) {
                        return loadValue(key);
                    }
                });
    }

    public static String getAccessToken(Long companyId) {
        try {
            return tokenCache.get(companyId);
        } catch (Exception e) {
            return null;
        }
    }
}
