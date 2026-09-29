package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FormAggregateTimeTransTypeEnum {
    DAY,
    MONTH,
    YEAR,
    WEEK,
    QUARTER;
}
