package com.wuji.service.client;

import lombok.Data;

@Data
public class PluginResult<T> {
    private String code;
    private Integer subCode;
    private String message;
    private T data;
    private Object otherData;

    public boolean isSuccess(String code) {
        return "0000".equals(code);
    }
}
