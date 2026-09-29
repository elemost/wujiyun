package com.wuji.factory.model.info;

import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.converter.AbstractFormDataFactoryExecuteConverter;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import com.wuji.service.model.request.factory.DataFactoryStageGroupFieldRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataFactoryGroupStage extends DataFactoryStage {

    private List<DataFactoryStageGroupFieldRequest> groupFields;

    private List<DataFactoryStageGroupFieldRequest> metricList;

    @Override
    public void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                        Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                        Map<String, FieldExistNameVO> formIdToMap) {
        if (CollectionUtils.isEmpty(metricList)) {
            return;
        }
        List<AggregationOperation> aggregationOperations = stageIdToAggregationListMap.get(this.getInput().get(0));
        List<Field> fields = new ArrayList<>();
        List<MongodbSearchField> mongodbSearchFieldList = new ArrayList<>();
        for (DataFactoryStageGroupFieldRequest fieldRequest : metricList) {
            fieldRequest.setTag(fieldRequest.getAliasName());
        }
        for (DataFactoryStageGroupFieldRequest fieldRequest : groupFields) {
            MongodbSearchField mongodbSearchField =
                    AbstractFormDataFactoryExecuteConverter.INSTANCE.toField(fieldRequest);
            fieldRequest.setTag(fieldRequest.getAliasName());
            mongodbSearchField.setTag(fieldRequest.getAliasName());
            mongodbSearchFieldList.add(mongodbSearchField);
        }
        List<Criteria> criteriaList = new ArrayList<>();
        for (MongodbSearchField mongodbSearchField : groupFields) {
            String fieldId = MongoSearchUtils.getFieldId(mongodbSearchField.getName(), mongodbSearchField.getType());
            criteriaList.add(Criteria.where(fieldId).ne(null));
        }
        MongoSearchUtils.addMatch(criteriaList, aggregationOperations);
        MongoSearchUtils.buildFieldList(fields, mongodbSearchFieldList);
        List<String> groupDateList = MongoSearchUtils.coverDateReturn(mongodbSearchFieldList, aggregationOperations);
        MongoSearchUtils.coverAddress(mongodbSearchFieldList, fields);
        for (String groupDate : groupDateList) {
            fields.add(Fields.field(groupDate));
        }
        Fields from = MongoSearchUtils.fieldToFields(fields);
        GroupOperation group = Aggregation.group(from);
        List<MongodbSearchField> metricFieldList =
                metricList.stream().map(AbstractFormDataFactoryExecuteConverter.INSTANCE::toField)
                        .collect(Collectors.toList());
        group = MongoSearchUtils.calculate(group, metricFieldList, true);
        aggregationOperations.add(group);
        ProjectionOperation fieldDocument = getProject(fields);
        aggregationOperations.add(fieldDocument);
        stageIdToAggregationListMap.put(this.getId(), aggregationOperations);
    }

    private ProjectionOperation getProject(List<Field> fields) {
        ProjectionOperation project = Aggregation.project();
        for (DataFactoryStageGroupFieldRequest mongodbSearchField : groupFields) {
            String field = MongoSearchUtils.getField(mongodbSearchField.getTag(), "", "");
            if (fields.size() == 1) {
                if (FormFieldTypeEnum.INPUT_DATE.getFieldType().equals(mongodbSearchField.getType())) {
                    Document document = MongoFunctionUtils.dateFromPart("$_id", null, null, null);
                    AggregationExpression from = AggregationExpression.from(MongoExpression.create(document.toJson()));
                    project = project.and(from).as(field);
                } else {
                    project = project.and("_id").as(field);
                }
            } else {
                if (FormFieldTypeEnum.INPUT_DATE.getFieldType().equals(mongodbSearchField.getType())) {
                    Document document = MongoFunctionUtils.dateFromPart(mongodbSearchField.getGroupType(),
                            "_id." + mongodbSearchField.getTag());
                    AggregationExpression from = AggregationExpression.from(MongoExpression.create(document.toJson()));
                    project = project.and(from).as(field);
                } else {
                    project = project.and("_id." + mongodbSearchField.getTag()).as(field);
                }
            }
            buildReturnField(mongodbSearchField, mongodbSearchField.getType());
        }
        for (DataFactoryStageGroupFieldRequest mongodbSearchField : metricList) {
            String field = MongoSearchUtils.getField(mongodbSearchField.getTag(), "", "");
            project = project.and(mongodbSearchField.getTag()).as(field);
            buildReturnField(mongodbSearchField, FormFieldTypeEnum.INPUT_NUMBER.getFieldType());
        }
        return project;
    }

    private void buildReturnField(DataFactoryStageGroupFieldRequest mongodbSearchField, String type) {
        DataFactoryReturnFieldVO dataFactoryReturnFieldVO = new DataFactoryReturnFieldVO();
        dataFactoryReturnFieldVO.setName(mongodbSearchField.getTag());
        dataFactoryReturnFieldVO.setLabel(mongodbSearchField.getLabel());
        dataFactoryReturnFieldVO.setFieldType(type);
        dataFactoryReturnFieldVO.setAliasName(mongodbSearchField.getAliasName());
        dataFactoryReturnFieldVO.setGroupType(mongodbSearchField.getGroupType());
        this.getReturnFields().add(dataFactoryReturnFieldVO);
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
