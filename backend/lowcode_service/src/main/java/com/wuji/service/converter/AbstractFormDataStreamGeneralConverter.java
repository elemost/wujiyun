package com.wuji.service.converter;

import com.wuji.plugin.model.info.config.HttpPluginConfig;
import com.wuji.service.model.info.plugin.HttpPlugin;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormDataStreamGeneralConverter {
    public static final AbstractFormDataStreamGeneralConverter INSTANCE =
            Mappers.getMapper(AbstractFormDataStreamGeneralConverter.class);

    public abstract HttpPluginConfig toRequest(HttpPlugin httpPlugin);
}
