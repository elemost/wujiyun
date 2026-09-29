package com.wuji.service.model.vo.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MongoSameNameVO {
    private String fieldId;

    private String label;

    private String subForm;

    private Object currentValue;
}
