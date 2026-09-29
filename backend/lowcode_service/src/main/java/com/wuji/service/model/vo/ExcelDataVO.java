package com.wuji.service.model.vo;

import lombok.Data;

@Data
public class ExcelDataVO {
    private String key;

    private Object value;

    private String type;

    private Integer colSpan;

    private Integer rowSpan;
}
