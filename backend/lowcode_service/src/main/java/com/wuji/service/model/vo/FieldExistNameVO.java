package com.wuji.service.model.vo;

import com.wuji.service.model.info.FormConfigCommon;
import lombok.Data;

import java.util.List;

@Data
public class FieldExistNameVO {
    private List<FormConfigCommon> fields;

    private String name;

    private String formId;

    private String applicationId;

    private String tableName;

    private String key;
}
