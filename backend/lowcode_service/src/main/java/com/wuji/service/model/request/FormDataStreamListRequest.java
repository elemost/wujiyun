package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormDataStreamListRequest {
    private String formId;

    private String applicationId;

    private String name;
}
