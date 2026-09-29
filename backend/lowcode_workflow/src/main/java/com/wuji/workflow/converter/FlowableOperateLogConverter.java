package com.wuji.workflow.converter;

import com.wuji.workflow.model.entity.FlowableOperateLogEntity;
import com.wuji.workflow.model.request.FlowableOperateLogRequest;
import com.wuji.workflow.model.vo.FlowableOperateLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class FlowableOperateLogConverter {
    public static final FlowableOperateLogConverter INSTANCE = Mappers.getMapper(FlowableOperateLogConverter.class);

    public abstract FlowableOperateLogEntity toEntity(FlowableOperateLogRequest flowableOperateLogRequest);

    public abstract FlowableOperateLogVO toVO(FlowableOperateLogEntity flowableOperateLogEntity);

}
