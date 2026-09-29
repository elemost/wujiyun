package com.wuji.factory.model.info;

import com.alibaba.fastjson.JSONArray;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.enums.DataFactoryStageReturnTypeEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.mongo.LookupAggregation;
import com.wuji.service.model.mongo.ProjectAggregation;
import com.wuji.service.model.mongo.UnionWithAggregation;
import com.wuji.service.model.request.factory.DataFactoryRelationRequest;
import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
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
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataFactoryJoinStage extends DataFactoryStage {

    // left_outer  right_outer inner full
    private String join;

    private Boolean mergeRelField = Boolean.FALSE;

    private List<DataFactoryRelationRequest> relation = new ArrayList<>();

    @Override
    public void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                        Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                        Map<String, FieldExistNameVO> formIdToMap) {
        if ("full".equals(join)) {
            fullJoin(idToStageMap, stageIdToAggregationListMap, formIdToMap);
        } else {
            withoutFull(idToStageMap, stageIdToAggregationListMap, formIdToMap, join);
        }
    }

    private void fullJoin(Map<String, DataFactoryStage> idToStageMap,
                          Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                          Map<String, FieldExistNameVO> formIdToMap) {
        List<String> input = this.getInput();
        String leftStage = input.get(0);
        String rightStage = input.get(1);
        List<AggregationOperation> aggregationOperationList =
                new ArrayList<>(stageIdToAggregationListMap.getOrDefault(leftStage, new ArrayList<>()));
        DataFactoryStage rightDataFactoryStage = idToStageMap.get(rightStage);
        DataFactoryStage leftDataFactoryStage = idToStageMap.get(leftStage);
        List<DataFactoryRelationRequest> leftRelations =
                JSONArray.parseArray(JSONArray.toJSONString(getRelation()), DataFactoryRelationRequest.class);
        addDateField(leftRelations, false, aggregationOperationList);
        LookupAggregation lookupAggregation =
                lookUp(idToStageMap, stageIdToAggregationListMap, formIdToMap, rightStage, rightDataFactoryStage,
                        leftRelations);
        aggregationOperationList.add(lookupAggregation);
        List<DataFactoryRelationRequest> rightRelations =
                JSONArray.parseArray(JSONArray.toJSONString(getRelation()), DataFactoryRelationRequest.class);
        buildRightRelation(rightRelations);
        unwindAndProject(join, aggregationOperationList, leftDataFactoryStage, rightDataFactoryStage);
        List<AggregationOperation> rightAggregationList = stageIdToAggregationListMap.get(rightStage);
        addDateField(rightRelations, false, rightAggregationList);
        LookupAggregation rightLookupAggregation =
                lookUp(idToStageMap, stageIdToAggregationListMap, formIdToMap, leftStage, leftDataFactoryStage,
                        rightRelations);
        rightAggregationList.add(rightLookupAggregation);
        String formId = rightDataFactoryStage.getFormId(idToStageMap);
        AggregationExpression aggregationExpression = AggregationExpression.from(
                MongoExpression.create(String.format("{ \"%s\": { \"$size\": 0 } }", getId())));
        MatchOperation match = Aggregation.match(aggregationExpression);
        rightAggregationList.add(match);
        ProjectionOperation fieldDocument =
                getFullDocument(leftDataFactoryStage.getReturnFields(), rightDataFactoryStage.getReturnFields());
        rightAggregationList.add(fieldDocument);
        UnionWithAggregation unionWithAggregation = new UnionWithAggregation(formIdToMap.get(formId).getTableName(),
                MongoFunctionUtils.toDocument(rightAggregationList));
        aggregationOperationList.add(unionWithAggregation);

        stageIdToAggregationListMap.put(this.getId(), aggregationOperationList);
        buildReturnField(leftDataFactoryStage, rightDataFactoryStage, idToStageMap);
    }

    private void withoutFull(Map<String, DataFactoryStage> idToStageMap,
                             Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                             Map<String, FieldExistNameVO> formIdToMap, String join) {
        List<String> input = this.getInput();
        String leftStage = input.get(0);
        String rightStage = input.get(1);
        List<DataFactoryRelationRequest> dataFactoryRelationRequests =
                JSONArray.parseArray(JSONArray.toJSONString(getRelation()), DataFactoryRelationRequest.class);
        if ("right_outer".equals(join)) {
            leftStage = input.get(1);
            rightStage = input.get(0);
            buildRightRelation(dataFactoryRelationRequests);
        }
        List<AggregationOperation> aggregationOperationList =
                new ArrayList<>(stageIdToAggregationListMap.getOrDefault(leftStage, new ArrayList<>()));
        DataFactoryStage rightDataFactoryStage = idToStageMap.get(rightStage);
        DataFactoryStage leftDataFactoryStage = idToStageMap.get(leftStage);
        addDateField(relation, false, aggregationOperationList);
        LookupAggregation lookupAggregation =
                lookUp(idToStageMap, stageIdToAggregationListMap, formIdToMap, rightStage, rightDataFactoryStage,
                        dataFactoryRelationRequests);
        aggregationOperationList.add(lookupAggregation);
        unwindAndProject(join, aggregationOperationList, leftDataFactoryStage, rightDataFactoryStage);
        stageIdToAggregationListMap.put(this.getId(), aggregationOperationList);
        buildReturnField(leftDataFactoryStage, rightDataFactoryStage, idToStageMap);
    }

    private void unwindAndProject(String join, List<AggregationOperation> aggregationOperationList,
                                  DataFactoryStage leftDataFactoryStage, DataFactoryStage rightDataFactoryStage) {
        UnwindOperation unwindOperation;
        if ("inner".equals(join)) {
            unwindOperation = Aggregation.unwind(getId(), false);
        } else {
            unwindOperation = Aggregation.unwind(getId(), true);
        }
        aggregationOperationList.add(unwindOperation);
        Document fieldDocument =
                getDocument(leftDataFactoryStage.getReturnFields(), rightDataFactoryStage.getReturnFields());
        ProjectAggregation projectAggregation = new ProjectAggregation(fieldDocument);
        aggregationOperationList.add(projectAggregation);
    }

    private static void buildRightRelation(List<DataFactoryRelationRequest> dataFactoryRelationRequests) {
        for (DataFactoryRelationRequest dataFactoryRelationRequest : dataFactoryRelationRequests) {
            String rightField = dataFactoryRelationRequest.getRightField();
            dataFactoryRelationRequest.setRightField(dataFactoryRelationRequest.getLeftField());
            dataFactoryRelationRequest.setLeftField(rightField);
        }
    }

    private LookupAggregation lookUp(Map<String, DataFactoryStage> idToStageMap,
                                     Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                                     Map<String, FieldExistNameVO> formIdToMap, String rightStage,
                                     DataFactoryStage rightDataFactoryStage,
                                     List<DataFactoryRelationRequest> relations) {
        List<AggregationOperation> aggregationOperations = stageIdToAggregationListMap.get(rightStage);
        addDateField(relations, true, aggregationOperations);
        for (DataFactoryRelationRequest dataFactoryRelationRequest : relations) {
            dataFactoryRelationRequest.setAliasLeftField(dataFactoryRelationRequest.getLeftField());
            dataFactoryRelationRequest.setAliasRightField(dataFactoryRelationRequest.getRightField());
        }
        List<Document> documentList = MongoFunctionUtils.toDocument(aggregationOperations);
        documentList.add(MongoFunctionUtils.pipeline(relations));
        String rightFormId = rightDataFactoryStage.getFormId(idToStageMap);
        return new LookupAggregation(formIdToMap.get(rightFormId).getTableName(), documentList,
                MongoFunctionUtils.let(relations), getId());
    }

    private void addDateField(List<DataFactoryRelationRequest> relations, Boolean right,
                              List<AggregationOperation> aggregationOperations) {
        AddFieldsOperation.AddFieldsOperationBuilder addFieldsOperationBuilder = Aggregation.addFields();
        List<DataFactoryRelationRequest> relationRequests = relations.stream()
                .filter(c -> FormFieldTypeEnum.INPUT_DATE.getFieldType().equals(c.getRightFieldType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(relationRequests)) {
            return;
        }
        for (DataFactoryRelationRequest dataFactoryRelationRequest : relationRequests) {
            if (right) {
                String rightField = MongoSearchUtils.getField(dataFactoryRelationRequest.getRightField(), "",
                        dataFactoryRelationRequest.getRightFieldType());
                Document document =
                        MongoFunctionUtils.dateTrunc("$" + rightField, dataFactoryRelationRequest.getPrecision());
                addFieldsOperationBuilder = addFieldsOperationBuilder.addField(rightField + "_date")
                        .withValueOfExpression(document.toJson());
            } else {
                String leftField = MongoSearchUtils.getField(dataFactoryRelationRequest.getLeftField(), "",
                        dataFactoryRelationRequest.getLeftFieldType());
                Document document =
                        MongoFunctionUtils.dateTrunc("$" + leftField, dataFactoryRelationRequest.getPrecision());
                addFieldsOperationBuilder = addFieldsOperationBuilder.addField(leftField + "_date")
                        .withValueOfExpression(document.toJson());
            }
        }
        aggregationOperations.add(addFieldsOperationBuilder.build());
    }

    private Document getDocument(List<DataFactoryReturnFieldVO> leftFieldList,
                                 List<DataFactoryReturnFieldVO> rightFieldList) {
        Document document = new Document();
        for (DataFactoryReturnFieldVO mainField : leftFieldList) {
            String fieldId = MongoSearchUtils.getField(mainField.getAliasName(), "", mainField.getFieldType());
            document.append(fieldId, 1);
        }
        List<String> mergeFields =
                relation.stream().map(DataFactoryRelationRequest::getRightField).collect(Collectors.toList());
        for (DataFactoryReturnFieldVO mainField : rightFieldList) {
            String fieldId = MongoSearchUtils.getField(mainField.getAliasName(), "", mainField.getFieldType());
            String aliasName = MongoSearchUtils.getField(mainField.getAliasName(), "", mainField.getAliasName());
            if (mergeRelField) {
                if (!mergeFields.contains(mainField.getAliasName())) {
                    document.append(fieldId, "$" + getId() + "." + aliasName);
                }
            } else {
                document.append(fieldId, "$" + getId() + "." + aliasName);
            }
        }
        return document;
    }

    private ProjectionOperation getFullDocument(List<DataFactoryReturnFieldVO> leftFieldList,
                                                List<DataFactoryReturnFieldVO> rightFieldList) {
        ProjectionOperation project = Aggregation.project();
        for (DataFactoryReturnFieldVO mainField : leftFieldList) {
            String fieldId = MongoSearchUtils.getField(mainField.getAliasName(), "", mainField.getFieldType());
            project = project.and(fieldId).as(fieldId);
        }
        List<String> mergeFields =
                relation.stream().map(DataFactoryRelationRequest::getRightField).collect(Collectors.toList());
        if (mergeRelField) {
            for (DataFactoryRelationRequest dataFactoryRelationRequest : relation) {
                String leftField = MongoSearchUtils.getField(dataFactoryRelationRequest.getLeftField(), "", "");
                String rightField = MongoSearchUtils.getField(dataFactoryRelationRequest.getRightField(), "", "");
                project = project.and(rightField).as(leftField);
            }
        }
        for (DataFactoryReturnFieldVO mainField : rightFieldList) {
            if (mergeRelField) {
                if (!mergeFields.contains(mainField.getAliasName())) {
                    String fieldId = MongoSearchUtils.getField(mainField.getAliasName(), "", mainField.getFieldType());
                    project = project.and(fieldId).as(fieldId);
                }
            } else {
                String fieldId = MongoSearchUtils.getField(mainField.getAliasName(), "", mainField.getFieldType());
                project = project.and(fieldId).as(fieldId);
            }
        }
        try {
            if (mergeRelField) {
                relation.stream().collect(Collectors.toMap(DataFactoryRelationRequest::getLeftField,
                        DataFactoryRelationRequest::getRightField));
            }
        } catch (Exception e) {
            throw new ServiceException(ServiceResultCode.FORM_DATA_FACTORY_CONFIG_ERROR,
                    "全连接的情况下，左边表单连接字段不能一致");
        }

        return project;
    }

    @Override
    public void sort(List<DataFactoryStage> dataFactoryStageList, Map<String, DataFactoryStage> idToStageMap) {
        if ("right_outer".equals(join)) {
            String leftInput = this.getInput().get(1);
            String rightInput = this.getInput().get(0);
            dataFactoryStageList.add(0, this);
            DataFactoryStage right = idToStageMap.get(rightInput);
            right.sort(dataFactoryStageList, idToStageMap);
            DataFactoryStage left = idToStageMap.get(leftInput);
            left.sort(dataFactoryStageList, idToStageMap);
        } else {
            String leftInput = this.getInput().get(0);
            String rightInput = this.getInput().get(1);
            dataFactoryStageList.add(0, this);
            DataFactoryStage right = idToStageMap.get(rightInput);
            right.sort(dataFactoryStageList, idToStageMap);
            DataFactoryStage left = idToStageMap.get(leftInput);
            left.sort(dataFactoryStageList, idToStageMap);
        }
    }

    @Override
    public String getFormId(Map<String, DataFactoryStage> idToStageMap) {
        String leftInput = null;
        if ("right_outer".equals(join)) {
            leftInput = this.getInput().get(1);
        } else {
            leftInput = this.getInput().get(0);
        }
        DataFactoryStage dataFactoryStage = idToStageMap.get(leftInput);
        return dataFactoryStage.getFormId(idToStageMap);
    }

    private void buildReturnField(DataFactoryStage leftDataFactoryStage, DataFactoryStage rightDataFactoryStage,
                                  Map<String, DataFactoryStage> idToStageMap) {
        List<DataFactoryRelationRequest> dataFactoryRelationRequests = this.getRelation();
        List<String> relFieldList = dataFactoryRelationRequests.stream().map(DataFactoryRelationRequest::getLeftField)
                .collect(Collectors.toList());
        relFieldList.addAll(dataFactoryRelationRequests.stream().map(DataFactoryRelationRequest::getRightField)
                .collect(Collectors.toList()));
        List<DataFactoryReturnFieldVO> returnFieldList = new ArrayList<>();
        setReturnFieldType(returnFieldList, leftDataFactoryStage, relFieldList,
                DataFactoryStageReturnTypeEnum.LEFT.name());
        setReturnFieldType(returnFieldList, rightDataFactoryStage, relFieldList,
                DataFactoryStageReturnTypeEnum.RIGHT.name());
        this.setReturnFields(returnFieldList);
    }

    private void setReturnFieldType(List<DataFactoryReturnFieldVO> returnFieldList,
                                    DataFactoryStage leftDataFactoryStage, List<String> relFieldList, String type) {
        for (DataFactoryReturnFieldVO dataFactoryReturnFieldVO : leftDataFactoryStage.getReturnFields()) {
            if (DataFactoryStageReturnTypeEnum.RIGHT.name().equals(type)) {
                if (relFieldList.contains(dataFactoryReturnFieldVO.getAliasName())) {
                    if (mergeRelField) {
                        continue;
                    }
                }
            }
            DataFactoryReturnFieldVO dataFactoryReturnField = new DataFactoryReturnFieldVO();
            dataFactoryReturnField.setName(dataFactoryReturnFieldVO.getName());
            dataFactoryReturnField.setAliasName(dataFactoryReturnFieldVO.getAliasName());
            dataFactoryReturnField.setLabel(dataFactoryReturnFieldVO.getLabel());
            dataFactoryReturnField.setFieldType(dataFactoryReturnFieldVO.getFieldType());
            if (!relFieldList.contains(dataFactoryReturnFieldVO.getAliasName())) {
                dataFactoryReturnField.setJoinFieldType(type);
                dataFactoryReturnField.setDisplayFormat(dataFactoryReturnFieldVO.getDisplayFormat());
                dataFactoryReturnField.setGroupType(dataFactoryReturnFieldVO.getGroupType());
            } else {
                dataFactoryReturnField.setJoinFieldType(DataFactoryStageReturnTypeEnum.REL.name());
                dataFactoryReturnField.setDisplayFormat(null);
                dataFactoryReturnField.setGroupType(null);
            }
            returnFieldList.add(dataFactoryReturnField);
        }
    }
}
