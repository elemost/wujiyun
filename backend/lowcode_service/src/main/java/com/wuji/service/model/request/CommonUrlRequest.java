package com.wuji.service.model.request;

import lombok.Data;

@Data
public class CommonUrlRequest {
    private String applicationId;

    private String formId;

    private String otherData;

    private String type;

    private String suiteId;
}
