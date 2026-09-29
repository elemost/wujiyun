package com.wuji.service.model.info;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FormFataFactorySyncConfig {

    private Boolean enable;

    // EXIST NEW
    private String syncFormType;

    private String formId;

    private String repeatTrigger;

    private String customCron;

    private Date startTime;

    // TIME
    private String syncType;

    private List<MappingField> mappingFields;


    @Data
    public static class MappingField {
        private String fieldId;

        private String fieldType;

        private String targetFieldId;

        private String targetFieldType;
    }
}
