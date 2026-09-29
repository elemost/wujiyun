package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DataStreamCalculateFnEnum {
    SUM(false),
    MAX(false),
    MIN(false),
    AVERAGE(false),
    COUNT(true);

    private final Boolean existNull;
}
