package com.wuji.admin.converter;

import com.wuji.admin.model.entity.ClientFunctionEntity;
import com.wuji.admin.model.vo.ClientFunctionVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractClientFunctionConverter {

    public static final AbstractClientFunctionConverter INSTANCE =
            Mappers.getMapper(AbstractClientFunctionConverter.class);

    public abstract ClientFunctionVO toVO(ClientFunctionEntity clientFunctionEntity);
}
