package com.wuji.common.model.vo;

import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.List;

@Data
public class FormDataFactoryParamVO {
    private List<AggregationOperation> aggregationOperations;

    private String tableName;

    private List<DataFactoryReturnFieldCommonVO> fields;
}

