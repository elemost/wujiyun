package com.wuji.common.model.vo;

import lombok.Data;

import java.util.List;
@Data
public class DataFactoryStageCommonFieldVO {
    private List<DataFactoryReturnFieldCommonVO> fields;

    private String title;

    private String id;
}
