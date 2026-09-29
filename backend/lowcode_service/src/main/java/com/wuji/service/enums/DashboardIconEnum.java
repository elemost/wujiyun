package com.wuji.service.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum DashboardIconEnum {
    PIVOT_TABLE;

    public static Boolean needCollection(String type) {
        List<String> needCollectionList = Lists.newArrayList(PIVOT_TABLE.name());
        return needCollectionList.contains(type);
    }
}
