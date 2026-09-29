package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormModelSaveRequest {
    private String formId;

    private String modelId;

    private Boolean publish;

    private String key;

    private String bpmnXml;

    private Boolean newVersion;
}
