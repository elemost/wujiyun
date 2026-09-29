package com.wuji.plugin.service.impl.auth;

import com.wuji.common.model.request.ApiRequest;
import com.wuji.common.utils.HttpUtils;
import com.wuji.plugin.enums.HttpPluginIdentityTypeEnum;
import com.wuji.plugin.model.info.HttpIdentityAuth;
import com.wuji.plugin.model.info.auth.ClientCredentials;
import com.wuji.plugin.service.AuthService;
import org.springframework.stereotype.Service;

@Service("clientCredentialsServiceImpl")
public class ClientCredentialsServiceImpl implements AuthService {

    @Override
    public String authType() {
        return HttpPluginIdentityTypeEnum.CLIENT_CREDENTIALS.name();
    }

    @Override
    public void getToken(HttpIdentityAuth httpIdentityAuth, ApiRequest apiRequest) {
        ClientCredentials clientCredentials = (ClientCredentials) httpIdentityAuth;
        ApiRequest tokenApiRequest = clientCredentials.getApiRequest();
        String token = HttpUtils.apiRequest(tokenApiRequest);
        apiRequest.putHeader("Authorization", token);
    }
}
