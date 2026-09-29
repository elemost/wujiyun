package com.wuji.service.converter;

import com.wuji.service.model.entity.TaskEntity;
import com.wuji.service.model.request.TaskCreateRequest;
import com.wuji.service.model.vo.TaskVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTaskConverter {
    public static final AbstractTaskConverter INSTANCE = Mappers.getMapper(AbstractTaskConverter.class);

    public abstract TaskEntity toEntity(TaskCreateRequest taskCreateRequest);

    public abstract TaskVO toVO(TaskEntity taskEntity);

}
