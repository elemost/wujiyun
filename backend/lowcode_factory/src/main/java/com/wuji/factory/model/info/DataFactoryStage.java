package com.wuji.factory.model.info;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import com.wuji.service.model.vo.FieldExistNameVO;
import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DataFactoryStage.class, visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = DataFactoryInputStage.class, name = "input"),
        @JsonSubTypes.Type(value = DataFactoryJoinStage.class, name = "join"),
        @JsonSubTypes.Type(value = DataFactoryOutPutStage.class, name = "output"),
        @JsonSubTypes.Type(value = DataFactoryGroupStage.class, name = "group"),
        @JsonSubTypes.Type(value = DataFactoryUnionStage.class, name = "union"),
        @JsonSubTypes.Type(value = DataFactoryProjectStage.class, name = "project"),
        @JsonSubTypes.Type(value = DataFactoryMatchStage.class, name = "filter"),
        @JsonSubTypes.Type(value = DataFactoryDistinctStage.class, name = "distinct"),
        @JsonSubTypes.Type(value = DataFactoryPivotStage.class, name = "pivot"),
        })
public abstract class DataFactoryStage {
    private String id;

    private List<String> input;

    private Integer posX;

    private Integer posY;

    private String type;

    private String title;

    private String remark;

    private String sourceStageId;

    private List<DataFactoryReturnFieldVO> returnFields = new ArrayList<>();

    private Boolean mainRoute = Boolean.FALSE;

    public abstract void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                                 Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                                 Map<String, FieldExistNameVO> formIdToMap);

    public abstract void sort(List<DataFactoryStage> dataFactoryStageList, Map<String, DataFactoryStage> idToStageMap);


    public abstract String getFormId(Map<String, DataFactoryStage> idToStageMap);

}
