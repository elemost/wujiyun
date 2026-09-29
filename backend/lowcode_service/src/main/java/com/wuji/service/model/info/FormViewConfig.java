package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FormViewConfig {

    // CUSTOM ALL
    private String showFieldType;

    private List<FormPrivilegeFieldConfig> fields;

    private String viewType;
}
