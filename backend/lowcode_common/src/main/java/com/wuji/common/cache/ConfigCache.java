package com.wuji.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.common.service.ConfigService;
import com.wuji.common.utils.ToolSpring;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigCache {

    private static final LoadingCache<String, String> configCache;

    static {
        configCache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
            private ConfigService configService;

            private synchronized String loadValue(String key) {
                if (configService == null) {
                    configService = ToolSpring.getBean(ConfigService.class);
                }

                return configService.detailByKey(key).getConfigValue();
            }

            @Override
            public String load(String key) {
                return loadValue(key);
            }
        });
    }

    public static String getValue(String configKey) {
        try {
            return configCache.get(configKey);
        } catch (Exception e) {
            log.error("get value error from cache by key[" + configKey + "]");
            return null;
        }
    }

    public static void clear(String key) {
        configCache.refresh(key);
    }
}
