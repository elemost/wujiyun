package com.wuji.service.converter;

import com.wuji.service.model.entity.FormDataFactoryEntity;
import com.wuji.service.model.entity.FormDataFactoryPublishEntity;
import com.wuji.service.model.request.FormDataFactoryPublishRequest;
import com.wuji.service.model.vo.FormDataFactoryPublishVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormDataFactoryPublishConverter {
    public static final AbstractFormDataFactoryPublishConverter INSTANCE =
            Mappers.getMapper(AbstractFormDataFactoryPublishConverter.class);

    public abstract FormDataFactoryPublishRequest toRequest(FormDataFactoryEntity formDataFactoryEntity);


    public abstract FormDataFactoryPublishEntity toEntity(FormDataFactoryPublishRequest formDataFactoryPublishRequest);

    public abstract FormDataFactoryPublishVO toVO(FormDataFactoryPublishEntity formDataFactoryPublishEntity);
}
