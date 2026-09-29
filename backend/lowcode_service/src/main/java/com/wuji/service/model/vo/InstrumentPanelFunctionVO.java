package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class InstrumentPanelFunctionVO {
    private String functionId;

    private List<String> fields;

    private String tagId;

    private String finalFunction;
}
