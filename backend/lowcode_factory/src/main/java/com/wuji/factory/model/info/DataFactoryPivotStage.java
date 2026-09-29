package com.wuji.factory.model.info;

import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.factory.model.domain.DataFactoryPivotField;
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
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
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
public class DataFactoryPivotStage extends DataFactoryStage {

    private List<DataFactoryStageGroupFieldRequest> groupFields;

    private List<DataFactoryStageGroupFieldRequest> metricList;


    private DataFactoryPivotField pivotField;

    @Override
    public void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                        Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                        Map<String, FieldExistNameVO> formIdToMap) {
        if (CollectionUtils.isEmpty(metricList) || CollectionUtils.isEmpty(groupFields) || pivotField == null ||
                CollectionUtils.isEmpty(pivotField.getPivotValues())) {
            return;
        }
        pivotField.setSrc(metricList.get(0).getAliasName());
        List<DataFactoryStageGroupFieldRequest> firstGroupFields = getFirstGroupFields();
        List<AggregationOperation> aggregationOperations = stageIdToAggregationListMap.get(this.getInput().get(0));
        List<Field> fields = new ArrayList<>();
        List<MongodbSearchField> mongodbSearchFieldList = new ArrayList<>();
        for (DataFactoryStageGroupFieldRequest fieldRequest : metricList) {
            fieldRequest.setTag(fieldRequest.getAliasName());
        }
        for (DataFactoryStageGroupFieldRequest fieldRequest : firstGroupFields) {
            MongodbSearchField mongodbSearchField =
                    AbstractFormDataFactoryExecuteConverter.INSTANCE.toField(fieldRequest);
            fieldRequest.setTag(fieldRequest.getAliasName());
            mongodbSearchField.setTag(fieldRequest.getAliasName());
            mongodbSearchFieldList.add(mongodbSearchField);
        }
        List<Criteria> criteriaList = new ArrayList<>();
        for (MongodbSearchField mongodbSearchField : firstGroupFields) {
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
        Map<String, String> nameMap = groupFields.stream().collect(
                Collectors.toMap(MongodbSearchField::getName, DataFactoryStageGroupFieldRequest::getAliasName));
        String field = nameMap.get(pivotField.getField());
        if (field != null) {
            pivotField.setField(field);
        }
        ProjectionOperation fieldDocument = getProject(fields, firstGroupFields);
        aggregationOperations.add(fieldDocument);
        pivot(aggregationOperations);
        stageIdToAggregationListMap.put(this.getId(), aggregationOperations);
    }

    private void pivot(List<AggregationOperation> aggregationOperations) {
        List<String> fields = new ArrayList<>();
        for (DataFactoryStageGroupFieldRequest groupField : groupFields) {
            String field = MongoSearchUtils.getField(groupField.getTag(), "", "");
            fields.add(field);
        }
        String k = MongoSearchUtils.getField(pivotField.getField(), "", "");
        String v = MongoSearchUtils.getField(pivotField.getSrc(), "", "");
        Document append = new Document().append("k", MongoFunctionUtils.toString(k)).append("v", "$" + v);
        GroupOperation secondGroup = Aggregation.group(fields.toArray(new String[0])).push(append).as("1111");
        aggregationOperations.add(secondGroup);
        AddFieldsOperation addFieldsOperation =
                Aggregation.addFields().addField("aaa").withValueOfExpression("{ $arrayToObject: \"$1111\" }").build();
        aggregationOperations.add(addFieldsOperation);
        ProjectionOperation project = Aggregation.project();
        List<DataFactoryReturnFieldVO> returnFields = new ArrayList<>();
        if (fields.size() == 1) {
            DataFactoryStageGroupFieldRequest groupField = groupFields.get(0);
            String field = MongoSearchUtils.getField(groupField.getAliasName(), "", "");
            project = project.and("_id").as(field);
            buildReturnField(groupField, returnFields);
        } else {
            for (DataFactoryStageGroupFieldRequest groupField : groupFields) {
                String field = MongoSearchUtils.getField(groupField.getAliasName(), "", "");
                project = project.and("_id." + groupField.getAliasName()).as(field);
                buildReturnField(groupField, returnFields);
            }
        }
        List<DataFactoryPivotField.PivotValue> pivotValues = pivotField.getPivotValues();
        for (DataFactoryPivotField.PivotValue pivotValue : pivotValues) {
            String field = MongoSearchUtils.getField(pivotValue.getAliasName(), "", "");
            project = project.and("aaa." + pivotValue.getValue()).as(field);
            DataFactoryReturnFieldVO dataFactoryReturnFieldVO = new DataFactoryReturnFieldVO();
            dataFactoryReturnFieldVO.setLabel(pivotValue.getValue());
            dataFactoryReturnFieldVO.setFieldType(pivotValue.getFieldType());
            dataFactoryReturnFieldVO.setAliasName(pivotValue.getAliasName());
            returnFields.add(dataFactoryReturnFieldVO);
        }
        aggregationOperations.add(project);
        setReturnFields(returnFields);
    }

    private static void buildReturnField(DataFactoryStageGroupFieldRequest groupField,
                                         List<DataFactoryReturnFieldVO> returnFields) {
        DataFactoryReturnFieldVO dataFactoryReturnFieldVO = new DataFactoryReturnFieldVO();
        dataFactoryReturnFieldVO.setLabel(groupField.getLabel());
        dataFactoryReturnFieldVO.setFieldType(groupField.getType());
        dataFactoryReturnFieldVO.setAliasName(groupField.getAliasName());
        returnFields.add(dataFactoryReturnFieldVO);
    }

    private ProjectionOperation getProject(List<Field> fields,
                                           List<DataFactoryStageGroupFieldRequest> firstGroupFields) {
        ProjectionOperation project = Aggregation.project();
        for (DataFactoryStageGroupFieldRequest mongodbSearchField : firstGroupFields) {
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
        }
        for (DataFactoryStageGroupFieldRequest mongodbSearchField : metricList) {
            String field = MongoSearchUtils.getField(mongodbSearchField.getTag(), "", "");
            project = project.and(mongodbSearchField.getTag()).as(field);
        }
        return project;
    }

    private List<DataFactoryStageGroupFieldRequest> getFirstGroupFields() {
        List<DataFactoryStageGroupFieldRequest> firstGroupFields = new ArrayList<>();
        firstGroupFields.addAll(groupFields);
        if (!groupFields.stream().map(MongodbSearchField::getName).collect(Collectors.toList())
                .contains(pivotField.getField())) {
            DataFactoryStageGroupFieldRequest dataFactoryStageGroupFieldRequest =
                    new DataFactoryStageGroupFieldRequest();
            dataFactoryStageGroupFieldRequest.setName(pivotField.getField());
            dataFactoryStageGroupFieldRequest.setTag(pivotField.getField());
            dataFactoryStageGroupFieldRequest.setAliasName(pivotField.getField());
            dataFactoryStageGroupFieldRequest.setType(pivotField.getType());
            firstGroupFields.add(dataFactoryStageGroupFieldRequest);
        }
        return firstGroupFields;
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
