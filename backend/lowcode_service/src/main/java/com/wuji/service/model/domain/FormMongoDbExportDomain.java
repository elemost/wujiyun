package com.wuji.service.model.domain;

import lombok.Data;

import java.util.List;

@Data
public class FormMongoDbExportDomain {
    private String label;

    private String name;

    private Integer row;

    private Integer column;

    private Integer maxRow;

    private Integer maxColumn;

    private String type;

    private List<FormMongoDbExportDomain> children;

}
