package com.wuji.common.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class DataFactoryStageInfoVO {
    private List<DataFactoryStageCommonFieldVO> fields;

    private String factoryName;
}
