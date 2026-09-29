package com.wuji.factory.model.info;

import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.utils.MongoSearchUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataFactoryMatchStage extends DataFactoryStage {

    private MongodbSearchFilter matchRule;

    @Override
    public void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                        Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                        Map<String, FieldExistNameVO> formIdToMap) {
        String input = this.getInput().get(0);
        List<AggregationOperation> aggregationOperationList = stageIdToAggregationListMap.get(input);
        MongoSearchUtils.filterValueIsEmpty(getMatchRule());
        Criteria criteria = MongoSearchUtils.buildCriteriaByFilter(matchRule, new ArrayList<>());
        if (criteria != null) {
            MatchOperation match = Aggregation.match(criteria);
            aggregationOperationList.add(match);
        }
        stageIdToAggregationListMap.put(this.getId(), aggregationOperationList);
        DataFactoryStage dataFactoryStage = idToStageMap.get(input);
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
