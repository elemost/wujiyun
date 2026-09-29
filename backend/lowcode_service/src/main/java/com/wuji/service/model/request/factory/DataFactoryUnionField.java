package com.wuji.service.model.request.factory;

import lombok.Data;

import java.util.Map;

@Data
public class DataFactoryUnionField {
    private String fieldId;

    private Map<String, String> fieldMapper;

    private String label;

    private String fieldType;
}
