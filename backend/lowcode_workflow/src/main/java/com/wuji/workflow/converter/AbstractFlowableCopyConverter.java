package com.wuji.workflow.converter;

import com.wuji.workflow.model.domain.FlowableCopyDomain;
import com.wuji.workflow.model.entity.FlowableCopyEntity;
import com.wuji.workflow.model.request.FlowableCopyCreateRequest;
import com.wuji.workflow.model.vo.FlowableCopyVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public  abstract class AbstractFlowableCopyConverter {
    public static final AbstractFlowableCopyConverter INSTANCE =
            Mappers.getMapper(AbstractFlowableCopyConverter.class);

    public abstract FlowableCopyEntity toEntity(FlowableCopyCreateRequest flowableCopyCreateRequest);

    public abstract FlowableCopyVO toVO(FlowableCopyDomain flowableCopyDomain);
}
