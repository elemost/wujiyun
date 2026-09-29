package com.wuji.systemapi.client.user;

import lombok.Data;

@Data
public class SystemResult<T> {
    private String code;
    private Integer subCode;
    private String message;
    private T data;


    public boolean isSuccess(String returnCode) {
        return "0000".equals(returnCode);
    }
}
