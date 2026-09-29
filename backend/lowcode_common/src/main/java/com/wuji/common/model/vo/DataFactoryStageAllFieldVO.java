package com.wuji.common.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class DataFactoryStageAllFieldVO {
    private List<DataFactoryReturnFieldCommonVO> fields;

    private String name;

    private String id;
}
