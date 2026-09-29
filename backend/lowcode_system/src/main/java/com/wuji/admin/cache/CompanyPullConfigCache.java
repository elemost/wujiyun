package com.wuji.admin.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.admin.service.CompanyPullConfigService;
import com.wuji.common.model.vo.CompanyPullConfigVO;
import com.wuji.common.utils.ToolSpring;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyPullConfigCache {
    private static final LoadingCache<String, CompanyPullConfigVO> configCache;

    static {
        configCache = CacheBuilder.newBuilder().build(new CacheLoader<String, CompanyPullConfigVO>() {
            private CompanyPullConfigService configService;

            private synchronized CompanyPullConfigVO loadValue(String key) {
                if (configService == null) {
                    configService = ToolSpring.getBean(CompanyPullConfigService.class);
                }
                String[] split = key.split("_");
                return configService.getByCompanyIdAndSuiteId(Long.valueOf(split[0]), split[1]);
            }

            @Override
            public CompanyPullConfigVO load(String key) {
                return loadValue(key);
            }
        });
    }

    public static CompanyPullConfigVO getValue(Long companyId, String suiteId) {
        try {
            return configCache.get(companyId + "_" + suiteId);
        } catch (Exception e) {
            log.error("get value error from cache by key[" + suiteId + "]");
            return new CompanyPullConfigVO();
        }
    }

    public static void clear(Long companyId, String suiteId) {
        configCache.refresh(companyId + "_" + suiteId);
    }
}
