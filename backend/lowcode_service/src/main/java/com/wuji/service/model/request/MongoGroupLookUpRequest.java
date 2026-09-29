package com.wuji.service.model.request;

import lombok.Data;

@Data
public class MongoGroupLookUpRequest {
    private String formId;

    private String groupId;

    private String applicationId;

    private String fieldId;

    private String subForm;

    private String fieldType;

    private String quoteFieldId;

    private String quoteFieldType;

    private String quoteSubForm;

    private String alias;
}
