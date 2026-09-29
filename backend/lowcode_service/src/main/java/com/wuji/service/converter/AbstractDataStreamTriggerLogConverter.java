package com.wuji.service.converter;

import com.wuji.service.model.domain.DataStreamTriggerLogDomain;
import com.wuji.service.model.domain.DataStreamTriggerLogStageDomain;
import com.wuji.service.model.vo.DataStreamTriggerLogStageVO;
import com.wuji.service.model.vo.DataStreamTriggerLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractDataStreamTriggerLogConverter {
    public static final AbstractDataStreamTriggerLogConverter INSTANCE =
            Mappers.getMapper(AbstractDataStreamTriggerLogConverter.class);

    public abstract DataStreamTriggerLogVO toVO(DataStreamTriggerLogDomain dataStreamTriggerLogDomain);

    public abstract DataStreamTriggerLogStageVO toVO(DataStreamTriggerLogStageDomain dataStreamTriggerLogStageDomain);
}
