package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormCreateRequest {

    private String id;

    private String sourceId;

    private String tableName;

    private String formType;

    private String applicationId;

}
