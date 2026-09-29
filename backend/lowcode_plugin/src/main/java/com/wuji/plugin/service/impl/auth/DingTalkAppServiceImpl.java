package com.wuji.plugin.service.impl.auth;

import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.teaopenapi.models.Config;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.common.model.request.ApiRequest;
import com.wuji.plugin.enums.HttpPluginIdentityTypeEnum;
import com.wuji.plugin.model.info.HttpIdentityAuth;
import com.wuji.plugin.model.info.auth.DingTalkApp;
import com.wuji.plugin.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DingTalkAppServiceImpl implements AuthService {

    @Override
    public String authType() {
        return HttpPluginIdentityTypeEnum.DING_TALK.name();
    }

    @Override
    public void getToken(HttpIdentityAuth httpIdentityAuth, ApiRequest apiRequest) {
        DingTalkApp dingTalkApp = (DingTalkApp) httpIdentityAuth;
        Client client = createClient();
        GetAccessTokenRequest getAccessTokenRequest = new GetAccessTokenRequest().setAppKey(dingTalkApp.getClientId())
                .setAppSecret(dingTalkApp.getClientSecret());
        try {
            GetAccessTokenResponse accessToken = client.getAccessToken(getAccessTokenRequest);
            String token = accessToken.getBody().getAccessToken();
            apiRequest.putQueryJson("suite_access_token", token);
        } catch (Exception err) {
            throw new AdminException(AdminResultCode.DING_TALK_TOKEN_ERROR);
        }
    }

    private static Client createClient() {
        try {
            Config config = new Config();
            config.protocol = "https";
            config.regionId = "central";
            return new Client(config);
        } catch (Exception e) {
            log.error("获取钉钉client失败", e);
            throw new AdminException(AdminResultCode.DING_TALK_CLIENT_ERROR);
        }
    }
}
