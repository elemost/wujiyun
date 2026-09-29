package com.wuji.factory.model.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataFactoryPivotField {

    private List<PivotValue> pivotValues = new ArrayList<>();

    private String field;

    private String type;

    private String label;

    private String src;

    @Data
    public static class PivotValue {
        private String value;

        private String aliasName;

        private String fieldType;
    }
}
