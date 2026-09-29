package com.wuji.service.model.vo;

import lombok.Data;

@Data
public class FormPendingCountVO {
    private String formName;

    private String applicationId;

    private String formId;

    private Integer pendingCount;

    private String applicationName;
}
