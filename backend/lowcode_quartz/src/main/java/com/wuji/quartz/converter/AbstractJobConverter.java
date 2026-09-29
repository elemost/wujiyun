package com.wuji.quartz.converter;

import com.wuji.quartz.model.entity.JobEntity;
import com.wuji.quartz.model.request.JobRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractJobConverter {
    public static final AbstractJobConverter INSTANCE = Mappers.getMapper(AbstractJobConverter.class);

    public abstract JobEntity toEntity(JobRequest jobRequest);
}
