package com.wuji.admin.converter;

import com.wuji.common.model.entity.ConfigEntity;
import com.wuji.common.model.request.ConfigSaveRequest;
import com.wuji.common.model.vo.ConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractConfigConverter {
    public static final AbstractConfigConverter INSTANCE = Mappers.getMapper(AbstractConfigConverter.class);

    public abstract ConfigEntity toEntity(ConfigSaveRequest configSaveRequest);

    public abstract ConfigVO toVO(ConfigEntity configEntity);
}
