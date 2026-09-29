package com.wuji.service.converter;

import com.wuji.service.model.entity.FormDataStreamEntity;
import com.wuji.service.model.entity.FormDataStreamPublishEntity;
import com.wuji.service.model.request.FormDataStreamPublishRequest;
import com.wuji.service.model.request.FormDataStreamUpdateRequest;
import com.wuji.service.model.vo.FormDataStreamPublishVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormDataStreamPublishConverter {
    public static final AbstractFormDataStreamPublishConverter INSTANCE =
            Mappers.getMapper(AbstractFormDataStreamPublishConverter.class);

    public abstract FormDataStreamPublishEntity toEntity(FormDataStreamUpdateRequest formDataStreamUpdateRequest);

    public abstract FormDataStreamPublishEntity toEntity(FormDataStreamPublishRequest formDataStreamPublishRequest);

    public abstract FormDataStreamPublishVO toVO(FormDataStreamPublishEntity formDataStreamPublishEntity);

    public abstract FormDataStreamPublishRequest toRequest(FormDataStreamEntity formDataStreamEntity);

}
