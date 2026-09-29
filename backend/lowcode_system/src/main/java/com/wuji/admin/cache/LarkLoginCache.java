package com.wuji.admin.cache;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.admin.client.lark.LarkClient;
import com.wuji.admin.client.lark.model.LarkDepartmentLoginRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.model.info.LarkConfig;
import com.wuji.common.utils.ToolSpring;

import java.util.concurrent.TimeUnit;

public class LarkLoginCache {
    private static final LoadingCache<Long, String> tokenCache;

    static {
        tokenCache =
                CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.HOURS).build(new CacheLoader<Long, String>() {
                    private LarkClient larkClient;

                    private CompanyService companyService;

                    private synchronized String loadValue(Long key) {
                        if (larkClient == null) {
                            larkClient = ToolSpring.getBean(LarkClient.class);
                        }
                        if (companyService == null) {
                            companyService = ToolSpring.getBean(CompanyService.class);
                        }
                        CompanyVO info = companyService.info(key);
                        LarkDepartmentLoginRequest larkDepartmentLoginRequest = new LarkDepartmentLoginRequest();
                        LarkConfig larkConfig = JSONObject.parseObject(info.getPullConfig(), LarkConfig.class);
                        larkDepartmentLoginRequest.setApp_id(larkConfig.getClientId());
                        larkDepartmentLoginRequest.setApp_secret(larkConfig.getClientSecret());
                        return "Bearer " + larkClient.login(larkDepartmentLoginRequest).getApp_access_token();
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
