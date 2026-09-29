package com.wuji.service.converter;

import com.wuji.service.model.info.FormRuleCondition;
import com.wuji.service.model.info.MongoFieldRelate;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchConditionQuote;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormUpdateDataRequest;
import com.wuji.service.model.request.FormWorkflowClaimRequest;
import com.wuji.service.model.request.FormWorkflowCreateTaskRequest;
import com.wuji.service.model.request.MongoGroupLookUpRequest;
import com.wuji.service.model.request.MongodbSearchRequest;
import com.wuji.service.model.vo.MongoDataFactoryHeaderVO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractMongoDbConverter {
    public static final AbstractMongoDbConverter INSTANCE = Mappers.getMapper(AbstractMongoDbConverter.class);

    public abstract MongodbSearchRequest toEntity(FormSearchDataRequest formSearchDataRequest);

    public abstract FormUpdateDataRequest toRequest(FormWorkflowClaimRequest formWorkflowClaimRequest);

    public abstract FormWorkflowCreateTaskRequest toRequest(FormInsertDataRequest formInsertDataRequest);

    public abstract FormWorkflowCreateTaskRequest toRequest(FormUpdateDataRequest formUpdateDataRequest);

    public abstract MongoDataFactoryHeaderVO toVO(MongoDataFactoryHeaderVO mongoDataFactoryHeaderVO);

    @Mapping(target = "alias", source = "quoteId")
    @Mapping(target = "quoteFieldId", source = "fieldId")
    @Mapping(target = "quoteFieldType", source = "fieldType")
    @Mapping(target = "quoteSubForm", source = "subForm")
    public abstract MongoGroupLookUpRequest toRequest(MongodbSearchConditionQuote mongodbSearchConditionQuote);


    @Mapping(target = "value", ignore = true)
    @Mapping(target = "type", source = "fieldType")
    public abstract MongodbSearchCondition toCondition(MongoFieldRelate mongoFieldRelate);

    @Mapping(target = "value", ignore = true)
    @Mapping(target = "type", source = "fieldType")
    public abstract MongodbSearchCondition toCondition(FormRuleCondition formRuleCondition);

    @AfterMapping
    void setField(MongoFieldRelate mongoFieldRelate, @MappingTarget MongodbSearchCondition mongodbSearchCondition) {
        if (StringUtils.isNotEmpty(mongoFieldRelate.getSubForm())) {
            mongodbSearchCondition.setFieldId(mongoFieldRelate.getSubForm());
            mongodbSearchCondition.setChildFieldId(mongoFieldRelate.getFieldId());
        } else {
            mongodbSearchCondition.setFieldId(mongoFieldRelate.getFieldId());
        }
    }

    @AfterMapping
    void setField(FormRuleCondition formRuleCondition, @MappingTarget MongodbSearchCondition mongodbSearchCondition) {
        if (StringUtils.isNotEmpty(formRuleCondition.getFieldSubForm())) {
            mongodbSearchCondition.setFieldId(formRuleCondition.getFieldSubForm());
            mongodbSearchCondition.setChildFieldId(formRuleCondition.getFieldId());
        } else {
            mongodbSearchCondition.setFieldId(formRuleCondition.getFieldId());
        }
    }

}
