package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormInfoTemplateRequest {
    private String formId;

    private String applicationId;

    private String infoId;

    private String uuid;
}
