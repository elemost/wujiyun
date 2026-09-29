package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormModelUpdateRequest {
    private String formId;

    private String applicationId;

    private String modelName;
}
