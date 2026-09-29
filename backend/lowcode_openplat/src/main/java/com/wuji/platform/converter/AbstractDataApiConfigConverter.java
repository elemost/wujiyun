package com.wuji.platform.converter;


import com.wuji.platform.model.entity.DataApiConfigEntity;
import com.wuji.platform.model.request.DataApiConfigCreateRequest;
import com.wuji.platform.model.request.DataApiConfigUpdateRequest;
import com.wuji.platform.model.vo.DataApiConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractDataApiConfigConverter {
    public static final AbstractDataApiConfigConverter INSTANCE =
            Mappers.getMapper(AbstractDataApiConfigConverter.class);

    public abstract DataApiConfigEntity toEntity(DataApiConfigCreateRequest dataApiConfigCreateRequest);

    public abstract DataApiConfigEntity toEntity(DataApiConfigUpdateRequest dataApiConfigUpdateRequest);

    public abstract DataApiConfigVO toVO(DataApiConfigEntity dataApiConfigEntity);

}
