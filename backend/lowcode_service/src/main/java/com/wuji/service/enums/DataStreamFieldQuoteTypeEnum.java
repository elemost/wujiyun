package com.wuji.service.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum DataStreamFieldQuoteTypeEnum {
    NODE_FIELD,
    CUSTOM,
    EMPTY,
    CALCULATE;

    public static List<String> needFieldQuoteTypeList() {
        return Lists.newArrayList(NODE_FIELD.name(), CALCULATE.name());
    }
}
