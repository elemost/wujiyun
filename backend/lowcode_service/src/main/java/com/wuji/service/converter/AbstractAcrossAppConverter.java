package com.wuji.service.converter;

import com.wuji.service.model.entity.AcrossAppEntity;
import com.wuji.service.model.request.AcrossAppSaveRequest;
import com.wuji.service.model.vo.AcrossAppVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractAcrossAppConverter {
    public static final AbstractAcrossAppConverter INSTANCE = Mappers.getMapper(AbstractAcrossAppConverter.class);

    public abstract AcrossAppEntity toEntity(AcrossAppSaveRequest acrossAppSaveRequest);

    public abstract AcrossAppVO toVO(AcrossAppEntity acrossAppEntity);

}
