package com.wuji.plugin.service.impl.auth;

import com.wuji.admin.client.lark.LarkClient;
import com.wuji.admin.client.lark.model.LarkDepartmentLoginRequest;
import com.wuji.common.model.request.ApiRequest;
import com.wuji.plugin.enums.HttpPluginIdentityTypeEnum;
import com.wuji.plugin.model.info.HttpIdentityAuth;
import com.wuji.plugin.model.info.auth.LarkApp;
import com.wuji.plugin.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LarkAppServiceImpl implements AuthService {

    @Autowired
    private LarkClient larkClient;

    @Override
    public String authType() {
        return HttpPluginIdentityTypeEnum.LARK.name();
    }

    @Override
    public void getToken(HttpIdentityAuth httpIdentityAuth, ApiRequest apiRequest) {
        LarkApp larkApp = (LarkApp) httpIdentityAuth;
        LarkDepartmentLoginRequest larkDepartmentLoginRequest = new LarkDepartmentLoginRequest();
        larkDepartmentLoginRequest.setApp_id(larkApp.getClientId());
        larkDepartmentLoginRequest.setApp_secret(larkApp.getClientSecret());
        apiRequest.putHeader("Authorization",
                "Bearer " + larkClient.login(larkDepartmentLoginRequest).getApp_access_token());
    }
}
