package com.wuji.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.common.utils.ToolSpring;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.service.ApplicationService;

import java.util.concurrent.TimeUnit;

public class ApplicationCache {
    private static final LoadingCache<String, ApplicationVO> configCache;

    static {
        configCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS)
                .build(new CacheLoader<String, ApplicationVO>() {

                    private ApplicationService applicationService;

                    private synchronized ApplicationVO loadValue(String key) {
                        if (applicationService == null) {
                            applicationService = ToolSpring.getBean(ApplicationService.class);
                        }
                        return applicationService.detail(key);
                    }

                    @Override
                    public ApplicationVO load(String key) {
                        return loadValue(key);
                    }
                });
    }

    public static Long getCompanyId(String id) {
        try {
            return configCache.get(id).getCompanyId();
        } catch (Exception e) {
            return null;
        }
    }

    public static ApplicationVO getDetail(String id) {
        try {
            return configCache.get(id);
        } catch (Exception e) {
            return null;
        }
    }

    public static void clear(String id) {
        configCache.refresh(id);
    }
}
