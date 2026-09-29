package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class MongodbSearchFilter {
    private List<MongodbSearchCondition> conditionList;

    private String rel;
}
