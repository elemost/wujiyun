package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormSerialNumberResetRequest {

    private Integer initialValue;

    private String cycle;

    private String applicationId;

    private String formId;

    private String fieldId;
}
