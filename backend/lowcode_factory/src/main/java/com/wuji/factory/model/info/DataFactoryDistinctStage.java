package com.wuji.factory.model.info;

import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.utils.MongoSearchUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ReplaceRootOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataFactoryDistinctStage extends DataFactoryStage {

    private List<DataFactoryReturnFieldVO> distinctFields = new ArrayList<>();

    @Override
    public void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                        Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                        Map<String, FieldExistNameVO> formIdToMap) {
        String input = this.getInput().get(0);
        DataFactoryStage dataFactoryStage = idToStageMap.get(input);
        List<AggregationOperation> aggregationOperationList = stageIdToAggregationListMap.get(input);
        String[] aliasArray =
                distinctFields.stream().map(c -> MongoSearchUtils.getField(c.getAliasName(), null, c.getFieldType()))
                        .toArray(String[]::new);
        GroupOperation groupOperation = Aggregation.group(aliasArray).first("$$ROOT").as("doc");
        aggregationOperationList.add(groupOperation);
        ReplaceRootOperation replaceRoot = Aggregation.replaceRoot("doc");
        aggregationOperationList.add(replaceRoot);
        stageIdToAggregationListMap.put(this.getId(), aggregationOperationList);
        this.setReturnFields(dataFactoryStage.getReturnFields());
    }

    @Override
    public void sort(List<DataFactoryStage> dataFactoryStageList, Map<String, DataFactoryStage> idToStageMap) {
        dataFactoryStageList.add(0, this);
        DataFactoryStage dataFactoryStage = idToStageMap.get(this.getInput().get(0));
        dataFactoryStage.sort(dataFactoryStageList, idToStageMap);
    }

    @Override
    public String getFormId(Map<String, DataFactoryStage> idToStageMap) {
        DataFactoryStage dataFactoryStage = idToStageMap.get(this.getInput().get(0));
        return dataFactoryStage.getFormId(idToStageMap);
    }
}
