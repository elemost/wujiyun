package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormMongoSyncUserRequest {
    private String uuid;

    private String buttonId;

    private String formId;

    private String applicationId;
}
