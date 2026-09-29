package com.wuji.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.common.utils.ToolSpring;
import com.wuji.service.model.vo.ManageVO;
import com.wuji.service.service.ManageService;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ManageCache {
    private static final LoadingCache<Long, Map<Long, ManageVO>> manageCache;

    static {
        manageCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.HOURS)
                .build(new CacheLoader<Long, Map<Long, ManageVO>>() {
                    private ManageService manageService;
                    private synchronized Map<Long, ManageVO> loadValue(Long key) {
                        if (manageService == null) {
                            manageService = ToolSpring.getBean(ManageService.class);
                        }
                        return manageService.userManegeMap(key);
                    }

                    @Override
                    public Map<Long, ManageVO> load(Long key) {
                        return loadValue(key);
                    }
                });
    }

    public static ManageVO getConfig(Long companyId, Long userId) {
        try {
            Map<Long, ManageVO> longManageVOMap = manageCache.get(companyId);
            return longManageVOMap.get(userId);
        } catch (Exception e) {
            return null;
        }
    }

    public static void clear(Long companyId) {
        manageCache.refresh(companyId);
    }
}
