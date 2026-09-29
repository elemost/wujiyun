package com.wuji.admin.converter;

import com.wuji.admin.model.domain.OperLogDomain;
import com.wuji.admin.model.entity.OperLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractOperLogConverter {
    public static final AbstractOperLogConverter INSTANCE = Mappers.getMapper(AbstractOperLogConverter.class);

    public abstract OperLogEntity toEntity(OperLogDomain operLogDomain);

}
