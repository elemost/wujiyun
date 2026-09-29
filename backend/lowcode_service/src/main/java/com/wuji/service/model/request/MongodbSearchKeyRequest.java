package com.wuji.service.model.request;

import lombok.Data;

@Data
public class MongodbSearchKeyRequest {
    private String fieldId;

    private String value;
}
