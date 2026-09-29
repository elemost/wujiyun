package com.wuji.service.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongodbSearchField {
    private String formId;

    private String label;

    private String type;

    private String parentName;

    private String name;

    private String op;

    private String format;

    private String formatType;

    private String growth;

    private String groupType;

    private Boolean hasPercent = false;

    private String tag;

    private String subForm;

    private String metricType;

    private String dataGroup;

    private String sortType;
}
