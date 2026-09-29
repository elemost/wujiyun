package com.wuji.service.model.request.factory;

import lombok.Data;

@Data
public class DataFactoryRelationRequest {

    private String leftField;

    private String leftFieldType;

    private String aliasLeftField;

    private String rightField;

    private String rightFieldType;

    private String aliasRightField;

    private String label;

    private String precision;
}
