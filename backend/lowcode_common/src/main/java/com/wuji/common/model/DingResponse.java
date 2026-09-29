package com.wuji.common.model;

import lombok.Data;

import java.util.Map;

@Data
public class DingResponse {
    private  Map<String, Object> data;


    protected DingResponse(Map<String, Object> data) {
        this.data = data;
    }

    public DingResponse() {
    }

    public static  DingResponse success(Map<String, Object> data) {
        return new DingResponse(data);
    }
}
