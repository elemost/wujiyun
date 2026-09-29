package com.wuji.service.converter;

import com.wuji.service.model.entity.FormDataFactoryInputEntity;
import com.wuji.service.model.request.FormDataFactoryInputRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormDataFactoryInputConverter {
    public static final AbstractFormDataFactoryInputConverter INSTANCE =
            Mappers.getMapper(AbstractFormDataFactoryInputConverter.class);


    public abstract FormDataFactoryInputEntity toEntity(FormDataFactoryInputRequest formDataFactoryInputRequest);
}
