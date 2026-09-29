package com.wuji.plugin.service.impl.plugin;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuji.common.model.request.ApiRequest;
import com.wuji.common.utils.HttpUtils;
import com.wuji.plugin.context.AuthContext;
import com.wuji.plugin.context.HttpResultContext;
import com.wuji.plugin.model.info.HttpIdentityAuth;
import com.wuji.plugin.model.info.PluginReturnMapping;
import com.wuji.plugin.model.info.config.HttpPluginConfig;
import com.wuji.plugin.model.vo.HttpPluginVO;
import com.wuji.plugin.service.PluginUseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HttpPluginUseImpl implements PluginUseService {

    @Autowired
    private HttpResultContext httpResultContext;

    @Autowired
    private AuthContext authContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String pluginType() {
        return "HTTP";
    }

    @Override
    public Object execute(Object object) {
        HttpPluginConfig httpPluginConfig;
        try {
            httpPluginConfig = objectMapper.readValue(JSONObject.toJSONString(object), HttpPluginConfig.class);
        } catch (Exception e) {
            log.error("转化http请求参数错误", e);
            return new HttpPluginVO();
        }
        HttpIdentityAuth identityAuth = httpPluginConfig.getIdentityAuth();
        ApiRequest apiRequest = httpPluginConfig.getApiRequest();
        if (identityAuth != null) {
            authContext.getHandler(identityAuth.getAuthType()).getToken(identityAuth, apiRequest);
        }
        String result = HttpUtils.apiRequest(apiRequest);
        PluginReturnMapping returnMapping = httpPluginConfig.getReturnMapping();
        JSONObject returnJson = new JSONObject();
        HttpPluginVO httpPluginVO = new HttpPluginVO();
        if (returnMapping != null) {
            httpResultContext.getHandler(returnMapping.getFieldType())
                    .buildHttpResponse(result, returnMapping, returnJson, returnMapping.getJsonPath());
        }
        httpPluginVO.setJsonObject(returnJson);
        httpPluginVO.setResult(result);
        return httpPluginVO;
    }
}
