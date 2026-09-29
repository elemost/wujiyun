package com.wuji.service.model.info;

import lombok.Data;

@Data
public class FormAggregateTableField {
    private String name;

    private String subForm;

    private String tag;

    private String text;

    private String type;

    private Boolean isJoined;

    private String format;
}
