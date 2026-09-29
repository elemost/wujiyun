package com.wuji.admin.singleton;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;

public class AliyunSmsClient {
    private static Client client;
    private static final String KeyId = "";
    private static final String KeySecret = "";

    private  AliyunSmsClient(){

    }

    public static synchronized Client getInstance() throws Exception {
        if(client == null){
            Config config = new Config()
                    .setAccessKeyId(KeyId)
                    .setAccessKeySecret(KeySecret)
                    .setEndpoint("dysmsapi.aliyuncs.com");
            client = new Client(config);
        }
        return client;
    }
}
