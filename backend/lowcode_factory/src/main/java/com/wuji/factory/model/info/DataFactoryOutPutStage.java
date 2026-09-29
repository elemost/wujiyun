package com.wuji.factory.model.info;

import com.wuji.service.model.vo.FieldExistNameVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataFactoryOutPutStage extends DataFactoryStage {

    private String output;

    @Override
    public void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                        Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                        Map<String, FieldExistNameVO> formIdToMap) {
        DataFactoryStage dataFactoryStage = idToStageMap.get(this.getInput().get(0));
        this.setReturnFields(dataFactoryStage.getReturnFields());
        stageIdToAggregationListMap.put(this.getId(), stageIdToAggregationListMap.get(this.getInput().get(0)));
    }

    @Override
    public void sort(List<DataFactoryStage> dataFactoryStageList, Map<String, DataFactoryStage> idToStageMap) {
        dataFactoryStageList.add(0, this);
        DataFactoryStage dataFactoryStage = idToStageMap.get(this.getInput().get(0));
        dataFactoryStage.sort(dataFactoryStageList, idToStageMap);
    }

    @Override
    public String getFormId(Map<String, DataFactoryStage> idToStageMap) {
        return null;
    }
}
