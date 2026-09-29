package com.wuji.admin.converter;

import com.wuji.admin.model.entity.LoginLogEntity;
import com.wuji.admin.model.request.LoginLogCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractLoginLogConverter {
    public static final AbstractLoginLogConverter INSTANCE = Mappers.getMapper(AbstractLoginLogConverter.class);

    public abstract LoginLogEntity toEntity(LoginLogCreateRequest loginLogCreateRequest);
}
