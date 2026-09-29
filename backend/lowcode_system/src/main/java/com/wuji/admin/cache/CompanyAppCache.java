package com.wuji.admin.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.admin.model.vo.CompanyAppDetailVO;
import com.wuji.admin.service.CompanyAppService;
import com.wuji.common.utils.ToolSpring;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyAppCache {
    private static final LoadingCache<Long, CompanyAppDetailVO> companyAppCache =
            CacheBuilder.newBuilder().build(new CacheLoader<Long, CompanyAppDetailVO>() {

                private CompanyAppService companyAppService;

                @Override
                public CompanyAppDetailVO load(Long companyId) {
                    if (companyAppService == null) {
                        companyAppService = ToolSpring.getBean(CompanyAppService.class);
                    }
                    return companyAppService.getCurrentClient();
                }
            });

    public static CompanyAppDetailVO getCompanyApp(Long id) {
        try {
            return companyAppCache.get(id);
        } catch (Exception e) {
            log.error("get value error from cache by key[" + id + "]", e);
            return new CompanyAppDetailVO();
        }
    }

    public static void cache(Long id, CompanyAppDetailVO companyAppDetailVO) {
        try {
            companyAppCache.refresh(id);
            companyAppCache.put(id, companyAppDetailVO);
        } catch (Exception e) {
            log.error("companyAppDetail get value error from cache by key[" + id + "]", e);
        }
    }

    public static void refresh(Long id) {
        companyAppCache.refresh(id);
    }
}
