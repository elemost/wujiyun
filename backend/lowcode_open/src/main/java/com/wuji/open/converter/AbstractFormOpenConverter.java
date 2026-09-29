package com.wuji.open.converter;

import com.wuji.open.model.request.FormDataQueryRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormOpenConverter {

    public static final AbstractFormOpenConverter INSTANCE = Mappers.getMapper(AbstractFormOpenConverter.class);

    public abstract FormSearchDataRequest toRequest(FormDataQueryRequest formDataQueryRequest);
}
