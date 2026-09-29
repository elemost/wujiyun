package com.wuji.admin.singleton;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;

public class TencentSmsClient {
    private static SmsClient smsClientInstance;
    private static final String KeyId = "";
    private static final String KeySecret = "";

    private TencentSmsClient(){
    }

    public static synchronized SmsClient getInstance(){
        if(smsClientInstance == null){
            Credential cred = new Credential(KeyId, KeySecret);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            smsClientInstance = new SmsClient(cred, "", clientProfile);
        }
        return smsClientInstance;
    }
    // 禁止克隆，防止通过克隆破坏单例
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Use getInstance() method to get the single instance of this class.");
    }
}
