package com.wuji.admin.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.admin.model.vo.CityVO;
import com.wuji.admin.service.CityService;
import com.wuji.common.utils.ToolSpring;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CityCache {
    private static LoadingCache<Integer, List<CityVO>> cityCache =
            CacheBuilder.newBuilder().build(new CacheLoader<Integer, List<CityVO>>() {

                private CityService cityService;

                @Override
                public List<CityVO> load(Integer companyId) {
                    if (cityService == null) {
                        cityService = ToolSpring.getBean(CityService.class);
                    }
                    return cityService.getList();
                }
            });
    ;


    public static List<CityVO> getCity() {
        try {
            return cityCache.get(0);
        } catch (Exception e) {
            log.error("获取城市缓存失败", e);
            return new ArrayList<>();
        }
    }

}
