package com.wuji.service.model.request;

import lombok.Data;

@Data
public class AcrossAppSaveRequest {

    private Long companyId;

    private String applicationId;

    private String formId;
}
