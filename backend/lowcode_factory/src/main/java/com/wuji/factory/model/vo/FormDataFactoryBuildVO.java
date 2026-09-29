package com.wuji.factory.model.vo;

import com.wuji.factory.model.info.DataFactoryStage;
import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FormDataFactoryBuildVO {
    private List<AggregationOperation> aggregationOperations;

    private String tableName;

    private Map<String, DataFactoryStage> stageIdToMap = new HashMap<>();
}
