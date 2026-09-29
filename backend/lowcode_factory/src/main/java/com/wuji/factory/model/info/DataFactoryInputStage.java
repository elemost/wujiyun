package com.wuji.factory.model.info;

import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.constant.Constants;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import com.wuji.service.model.request.factory.DataFactoryStageDataSourceRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.utils.MongoSearchUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataFactoryInputStage extends DataFactoryStage {

    private DataFactoryStageDataSourceRequest dataSource;

    private List<DataFactoryReturnFieldVO> mainFields;

    private List<DataFactoryReturnFieldVO> subFormFields = new ArrayList<>();

    @Override
    public void convert(Map<String, DataFactoryStage> idToStageMap, DataFactoryStage mainStage,
                        Map<String, List<AggregationOperation>> stageIdToAggregationListMap,
                        Map<String, FieldExistNameVO> formIdToMap) {
        List<AggregationOperation> aggregationOperationList = new ArrayList<>();
        List<Criteria> criteriaList = new ArrayList<>();
        MongoSearchUtils.buildCommonFilter(criteriaList, dataSource.getApplicationId(), dataSource.getFormId());
        MatchOperation match = Aggregation.match(new Criteria().andOperator(criteriaList));
        aggregationOperationList.add(match);
        String dataSubForm = this.getDataSource().getSubFormId();
        ProjectionOperation project = Aggregation.project();
        if (StringUtils.isNotEmpty(dataSubForm)) {
            String subForm = MongoSearchUtils.getFieldId(dataSubForm, Constants.SUB_FORM_TYPE);
            UnwindOperation unwindOperation = new UnwindOperation(Fields.field("$" + subForm), Boolean.TRUE);
            aggregationOperationList.add(unwindOperation);
            for (DataFactoryReturnFieldVO subFormField : subFormFields) {
                String fieldId = buildFieldId(dataSubForm, subFormField, aggregationOperationList);
                String aliasName = MongoSearchUtils.getField(subFormField.getAliasName(), "",
                        subFormField.getFieldType());
                project = project.and(fieldId).as(aliasName);
            }
        }
        for (DataFactoryReturnFieldVO mainField : mainFields) {
            String fieldId = buildFieldId(null, mainField, aggregationOperationList);
            String aliasName = MongoSearchUtils.getFieldIdNotExistLogic(mainField.getAliasName(), "");
            project = project.and(fieldId).as(aliasName);
        }
        aggregationOperationList.add(project);
        stageIdToAggregationListMap.put(this.getId(), aggregationOperationList);
        List<DataFactoryReturnFieldVO> returnFieldList = getReturnFieldVOS(formIdToMap);
        this.setReturnFields(returnFieldList);
    }

    private List<DataFactoryReturnFieldVO> getReturnFieldVOS(Map<String, FieldExistNameVO> formIdToMap) {
        FieldExistNameVO fieldExistNameVO = formIdToMap.get(this.dataSource.getFormId());
        if (fieldExistNameVO == null) {
            return new ArrayList<>();
        }
        Map<String, FormConfigCommon> formCommonMap =
                fieldExistNameVO.getFields().stream().collect(Collectors.toMap(FormConfigCommon::getName, c -> c));
        List<DataFactoryReturnFieldVO> returnFieldList = new ArrayList<>();
        returnFieldList.addAll(mainFields);
        returnFieldList.addAll(subFormFields);
        for (DataFactoryReturnFieldVO fieldVO : returnFieldList) {
            FormConfigCommon formConfigCommon = formCommonMap.get(fieldVO.getName());
            if (formConfigCommon != null) {
                fieldVO.setLabel(formConfigCommon.getLabel());
                fieldVO.setSubFormLabel(formConfigCommon.getSubFromLabel());
                fieldVO.setDisplayFormat(formConfigCommon.getDisplayFormat());
            }
            if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE.getFieldType().equals(fieldVO.getFieldType())) {
                fieldVO.setFieldType(FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType());
            } else if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE.getFieldType().equals(fieldVO.getFieldType())) {
                fieldVO.setFieldType(FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType());
            } else if (FormSystemFieldEnum.CREATE_NAME.getName().equals(fieldVO.getName())) {
                fieldVO.setFieldType(FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType());
            } else if (FormSystemFieldEnum.CREATE_TIME.getName().equals(fieldVO.getName())) {
                fieldVO.setFieldType(FormFieldTypeEnum.INPUT_DATE.getFieldType());
            } else if (FormSystemFieldEnum.UPDATE_TIME.getName().equals(fieldVO.getName())) {
                fieldVO.setFieldType(FormFieldTypeEnum.INPUT_DATE.getFieldType());
            } else if (FormSystemFieldEnum.STATUS.getName().equals(fieldVO.getName())) {
                fieldVO.setFieldType(FormFieldTypeEnum.SYSTEM_STATUS.getFieldType());
            }
        }
        return returnFieldList;
    }

    private String buildFieldId(String subForm, DataFactoryReturnFieldVO field,
                                List<AggregationOperation> aggregationOperationList) {
        String fieldId = MongoSearchUtils.getField(field.getName(), subForm, field.getFieldType());
        if (FormSystemFieldEnum.CREATE_NAME.getName().equals(field.getName())) {
            fieldId = fieldId + ".assigneeId";
        }
        if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE.getFieldType().equals(field.getFieldType())) {
            UnwindOperation userUnwind = new UnwindOperation(Fields.field("$" + fieldId), Boolean.TRUE);
            aggregationOperationList.add(userUnwind);
            fieldId = fieldId + ".assigneeId";
        }
        if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE.getFieldType().equals(field.getFieldType())) {
            UnwindOperation deptUnwind = new UnwindOperation(Fields.field("$" + fieldId), Boolean.TRUE);
            aggregationOperationList.add(deptUnwind);
            fieldId = fieldId + ".value";
        }
        return fieldId;
    }

    @Override
    public void sort(List<DataFactoryStage> dataFactoryStageList, Map<String, DataFactoryStage> idToStageMap) {
        dataFactoryStageList.add(0, this);
    }

    @Override
    public String getFormId(Map<String, DataFactoryStage> idToStageMap) {
        return dataSource.getFormId();
    }

}
