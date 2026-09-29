package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormSerialNumberRequest {

    private String cycle;

    private String applicationId;

    private String formId;

    private String fieldId;
}
