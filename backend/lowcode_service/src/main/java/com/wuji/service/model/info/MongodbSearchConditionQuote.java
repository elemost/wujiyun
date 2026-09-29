package com.wuji.service.model.info;

import lombok.Data;

@Data
public class MongodbSearchConditionQuote {
    private String quoteId;

    private String formId;

    private String groupId;

    private String fieldId;

    private String subForm;

    private String fieldType;
}
