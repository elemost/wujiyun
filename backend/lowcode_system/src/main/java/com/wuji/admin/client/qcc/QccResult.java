package com.wuji.admin.client.qcc;

import lombok.Data;

@Data
public class QccResult<T> {

    private String status;

    private String msg;

    private T result;

    public boolean isSuccess(String returnCode) {
        return "0".equals(returnCode);
    }
}
