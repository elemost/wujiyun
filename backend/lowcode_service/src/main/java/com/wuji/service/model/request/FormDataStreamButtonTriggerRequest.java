package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormDataStreamButtonTriggerRequest {
    private String formId;

    private String applicationId;

    private String dataStreamId;

    private String uuid;
}
