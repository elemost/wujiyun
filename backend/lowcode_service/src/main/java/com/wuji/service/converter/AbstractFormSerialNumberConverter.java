package com.wuji.service.converter;

import com.wuji.service.model.entity.FormSerialNumberEntity;
import com.wuji.service.model.vo.FormSerialNumberVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormSerialNumberConverter {
    public static final AbstractFormSerialNumberConverter INSTANCE =
            Mappers.getMapper(AbstractFormSerialNumberConverter.class);

    public abstract FormSerialNumberVO toEntity(FormSerialNumberEntity formSerialNumberEntity);
}
