package com.wuji.plugin.service.impl.auth;

import com.wuji.admin.client.wecom.WeComClient;
import com.wuji.common.model.request.ApiRequest;
import com.wuji.plugin.enums.HttpPluginIdentityTypeEnum;
import com.wuji.plugin.model.info.HttpIdentityAuth;
import com.wuji.plugin.model.info.auth.WeComApp;
import com.wuji.plugin.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeComAppServiceImpl implements AuthService {

    @Autowired
    private WeComClient weComClient;

    @Override
    public String authType() {
        return HttpPluginIdentityTypeEnum.WECOM.name();
    }

    @Override
    public void getToken(HttpIdentityAuth httpIdentityAuth, ApiRequest apiRequest) {
        WeComApp weComApp = (WeComApp) httpIdentityAuth;
        String accessToken =
                weComClient.getAccessToken(weComApp.getCorpId(), weComApp.getClientSecret()).getAccess_token();
        apiRequest.putQueryJson("access_token", accessToken);
    }

}
