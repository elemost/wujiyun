package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormFieldRequest {
    private String fieldId;

    private String subForm;

    private String fieldType;
}
