package com.wuji.service.model.vo;

import lombok.Data;

@Data
public class ExcelImportDataErrorVO {
    private Integer row;

    private Integer column;

    private String errorMessage;
}
