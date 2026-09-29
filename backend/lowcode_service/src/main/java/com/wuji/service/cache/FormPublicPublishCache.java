package com.wuji.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.common.utils.ToolSpring;
import com.wuji.service.model.vo.FormPublicPublishVO;
import com.wuji.service.service.FormPublicPublishService;

import java.util.concurrent.TimeUnit;


public class FormPublicPublishCache {
    private static final LoadingCache<String, FormPublicPublishVO> configCache;

    static {
        configCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.HOURS)
                .build(new CacheLoader<String, FormPublicPublishVO>() {

                    private FormPublicPublishService formPublicPublishService;

                    private synchronized FormPublicPublishVO loadValue(String key) {
                        if (formPublicPublishService == null) {
                            formPublicPublishService = ToolSpring.getBean(FormPublicPublishService.class);
                        }
                        String[] split = key.split("_");
                        return formPublicPublishService.info(split[1], split[0], "FORM_FILL");
                    }

                    @Override
                    public FormPublicPublishVO load(String key) {
                        return loadValue(key);
                    }
                });
    }

    public static FormPublicPublishVO getConfig(String id) {
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
