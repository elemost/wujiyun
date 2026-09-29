package com.wuji.factory.model.info;

import com.wuji.service.model.mongo.UnionWithAggregation;
import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import com.wuji.service.model.request.factory.DataFactoryUnionField;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataFactoryUnionStage extends DataFactoryStage {

    private List<DataFactoryUnionField> fields = new ArrayList<>();

    @Override
    public void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                        Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                        Map<String, FieldExistNameVO> formIdToMap) {
        List<String> input = this.getInput();
        String firstStage = input.get(0);
        List<AggregationOperation> firstAggregationList = stageIdToAggregationListMap.get(firstStage);
        getUnionAggregation(firstAggregationList, firstStage);
        for (int i = 1; i < getInput().size(); i++) {
            String otherStage = input.get(i);
            List<AggregationOperation> otherAggregationList = stageIdToAggregationListMap.get(otherStage);
            DataFactoryStage otherDataFactoryStage = idToStageMap.get(otherStage);
            String formId = otherDataFactoryStage.getFormId(idToStageMap);
            getUnionAggregation(otherAggregationList, otherStage);
            UnionWithAggregation unionWithAggregation = new UnionWithAggregation(formIdToMap.get(formId).getTableName(),
                    MongoFunctionUtils.toDocument(otherAggregationList));
            firstAggregationList.add(unionWithAggregation);
        }

        stageIdToAggregationListMap.put(this.getId(), firstAggregationList);
        buildReturnField();
    }

    private void getUnionAggregation(List<AggregationOperation> otherAggregationList, String stageId) {
        ProjectionOperation project = Aggregation.project();
        for (DataFactoryUnionField dataFactoryUnionField : fields) {
            if (dataFactoryUnionField.getFieldMapper() == null) {
                continue;
            }
            if (dataFactoryUnionField.getFieldMapper().get(stageId) == null) {
                continue;
            }
            String aliasName = MongoSearchUtils.getField(dataFactoryUnionField.getFieldId(), "",
                    dataFactoryUnionField.getFieldType());
            String fieldId = MongoSearchUtils.getField(dataFactoryUnionField.getFieldMapper().get(stageId), "",
                    dataFactoryUnionField.getFieldType());
            project = project.and(fieldId).as(aliasName);
        }
        otherAggregationList.add(project);
    }

    private void buildReturnField() {
        List<DataFactoryReturnFieldVO> returnFields = new ArrayList<>();
        for (DataFactoryUnionField dataFactoryUnionField : fields) {
            DataFactoryReturnFieldVO dataFactoryReturnFieldVO = new DataFactoryReturnFieldVO();
            dataFactoryReturnFieldVO.setAliasName(dataFactoryUnionField.getFieldId());
            dataFactoryReturnFieldVO.setName(dataFactoryUnionField.getFieldId());
            dataFactoryReturnFieldVO.setLabel(dataFactoryUnionField.getLabel());
            dataFactoryReturnFieldVO.setFieldType(dataFactoryUnionField.getFieldType());
            returnFields.add(dataFactoryReturnFieldVO);
        }
        setReturnFields(returnFields);
    }

    @Override
    public void sort(List<DataFactoryStage> dataFactoryStageList, Map<String, DataFactoryStage> idToStageMap) {
        dataFactoryStageList.add(0, this);
        for (int i = getInput().size(); i > 0; i--) {
            String stageId = getInput().get(i - 1);
            DataFactoryStage dataFactoryStage = idToStageMap.get(stageId);
            dataFactoryStage.sort(dataFactoryStageList, idToStageMap);
        }
    }

    @Override
    public String getFormId(Map<String, DataFactoryStage> idToStageMap) {
        DataFactoryStage dataFactoryStage = idToStageMap.get(this.getId());
        String input = dataFactoryStage.getInput().get(0);
        return idToStageMap.get(input).getFormId(idToStageMap);
    }
}
