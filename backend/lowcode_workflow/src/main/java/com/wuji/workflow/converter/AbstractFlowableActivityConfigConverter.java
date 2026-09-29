package com.wuji.workflow.converter;

import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
import com.wuji.workflow.model.entity.FlowableActivityConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFlowableActivityConfigConverter {
    public static final AbstractFlowableActivityConfigConverter INSTANCE =
            Mappers.getMapper(AbstractFlowableActivityConfigConverter.class);

    public abstract FlowableActivityConfigEntity toEntity(FlowableActivityConfigDomain flowableActivityConfigDomain);

    public abstract FlowableActivityConfigDomain toDomain(FlowableActivityConfigEntity flowableActivityConfigEntity);

}
