package com.wuji.admin.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.utils.ToolSpring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CompanyCache {


    private static LoadingCache<Long, CompanyVO> companyCache =
            CacheBuilder.newBuilder().build(new CacheLoader<Long, CompanyVO>() {

                private CompanyService companyService;

                @Override
                public CompanyVO load(Long companyId) {
                    if (companyService == null) {
                        companyService = ToolSpring.getBean(CompanyService.class);
                    }
                    return companyService.info(companyId);
                }
            });
    ;


    public static CompanyVO getCompany(Long id) {
        try {
            return companyCache.get(id);
        } catch (Exception e) {
            log.error("get value error from cache by key[" + id + "]", e);
            return new CompanyVO();
        }
    }

    public static void clear(Long id, String uuid) {
        try {
            companyCache.refresh(id);
            companyUuidCache.refresh(uuid);
        } catch (Exception e) {
            log.error("get value error from cache by key[" + id + "]", e);
        }
    }

    private static LoadingCache<String, CompanyVO> companyUuidCache =
            CacheBuilder.newBuilder().build(new CacheLoader<String, CompanyVO>() {

                private CompanyService companyService;

                @Override
                public CompanyVO load(String companyId) {
                    if (companyService == null) {
                        companyService = ToolSpring.getBean(CompanyService.class);
                    }
                    return companyService.infoByUuid(companyId);
                }
            });
    ;


    public static CompanyVO getCompanyByUuid(String uuid) {
        try {
            return companyUuidCache.get(uuid);
        } catch (Exception e) {
            log.error("get value error from cache by key[" + uuid + "]", e);
            return null;
        }
    }

}
