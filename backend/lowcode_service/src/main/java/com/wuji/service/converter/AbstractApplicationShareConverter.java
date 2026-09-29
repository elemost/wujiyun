package com.wuji.service.converter;

import com.wuji.service.model.entity.ApplicationShareEntity;
import com.wuji.service.model.vo.ApplicationShareVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractApplicationShareConverter {
    public static final AbstractApplicationShareConverter INSTANCE =
            Mappers.getMapper(AbstractApplicationShareConverter.class);

    public abstract ApplicationShareVO toVO(ApplicationShareEntity applicationShareEntity);
}
