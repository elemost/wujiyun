package com.wuji.admin.client.lark;

import lombok.Data;

@Data
public class LarkResult<T> {
    private int code;
    private String msg;
    private T Data;
    private String app_access_token;
    private String tenant_access_token;
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private Integer refresh_token_expires_in;
    private String scope;
    private String token_type;
    private Integer expire;


    public boolean isSuccess(Integer returnCode) {
        return 0 == returnCode;
    }
}
