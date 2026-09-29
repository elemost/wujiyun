package com.wuji.common.model;

import lombok.Data;

@Data
public class WeComResponse {
    private Object data;


    protected WeComResponse(Long data) {
        this.data = data;
    }
    protected WeComResponse(String  data) {
        this.data = data;
    }

    public WeComResponse() {
    }

    public static WeComResponse success(Long data) {
        return new WeComResponse(data);
    }

    public static WeComResponse success(String data) {
        return new WeComResponse(data);
    }
}
