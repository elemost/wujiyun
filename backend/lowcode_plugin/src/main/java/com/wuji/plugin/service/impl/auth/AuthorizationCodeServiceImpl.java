package com.wuji.plugin.service.impl.auth;

import com.wuji.common.model.request.ApiRequest;
import com.wuji.plugin.enums.HttpPluginIdentityTypeEnum;
import com.wuji.plugin.model.info.HttpIdentityAuth;
import com.wuji.plugin.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationCodeServiceImpl implements AuthService {

    @Override
    public String authType() {
        return HttpPluginIdentityTypeEnum.AUTHORIZATION_CODE.name();
    }

    @Override
    public void getToken(HttpIdentityAuth httpIdentityAuth, ApiRequest apiRequest) {
    }

}
