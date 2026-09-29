package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FormExtraFunctionSync {
    private String label;

    private String type;

    private String name;

    private List<FormExtraFunctionSync> columns;

    private String mappingField;
}
