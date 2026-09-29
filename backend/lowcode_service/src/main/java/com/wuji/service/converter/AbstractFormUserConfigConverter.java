package com.wuji.service.converter;

import com.wuji.service.model.entity.FormUserConfigEntity;
import com.wuji.service.model.request.FormUserConfigSaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormUserConfigConverter {

    public static final AbstractFormUserConfigConverter INSTANCE =
            Mappers.getMapper(AbstractFormUserConfigConverter.class);

    public abstract FormUserConfigEntity toEntity(FormUserConfigSaveRequest formUserConfigSaveRequest);
}
