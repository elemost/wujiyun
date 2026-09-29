package com.wuji.admin.client.wecom;

import lombok.Data;

@Data
public class WeComResult {
    private int errcode;
    private String errmsg;
    private String access_token;



    public boolean isSuccess(Integer errcode) {
        return 0 == errcode;
    }
}
