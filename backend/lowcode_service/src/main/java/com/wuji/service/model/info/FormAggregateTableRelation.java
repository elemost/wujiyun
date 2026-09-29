package com.wuji.service.model.info;

import lombok.Data;

import java.util.Map;

@Data
public class FormAggregateTableRelation {
    private String joinId;

    // 前面formId 后面fieldId
    private Map<String, String> relation;

    private Boolean isSubForm;
}
