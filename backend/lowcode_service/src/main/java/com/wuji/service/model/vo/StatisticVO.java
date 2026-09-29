package com.wuji.service.model.vo;

import lombok.Data;

@Data
public class StatisticVO {
    private Integer userCount = 0;

    private Integer applicationCount = 0;

    private Integer aggregateCount = 0;

    private Integer dataStreamCount = 0;

    private Integer dataFactoryCount = 0;

    private Integer formCount = 0;
}
