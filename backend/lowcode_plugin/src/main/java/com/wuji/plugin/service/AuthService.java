package com.wuji.plugin.service;

import com.wuji.common.model.request.ApiRequest;
import com.wuji.plugin.model.info.HttpIdentityAuth;

public interface AuthService {
    String authType();

    void getToken(HttpIdentityAuth httpIdentityAuth, ApiRequest apiRequest);
}
