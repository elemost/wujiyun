package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormModelUpdateStatusRequest {
    private String applicationId;

    private String formId;

    private String status;
}
