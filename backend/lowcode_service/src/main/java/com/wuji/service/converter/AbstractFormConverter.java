package com.wuji.service.converter;

import com.wuji.service.model.entity.FormEntity;
import com.wuji.service.model.request.FormCreateRequest;
import com.wuji.service.model.request.FormUpdateRequest;
import com.wuji.service.model.vo.FormFieldVO;
import com.wuji.service.model.vo.FormVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormConverter {
    public static final AbstractFormConverter INSTANCE = Mappers.getMapper(AbstractFormConverter.class);

    public abstract FormEntity toEntity(FormCreateRequest formCreateRequest);

    public abstract FormEntity toEntity(FormUpdateRequest formUpdateRequest);

    public abstract FormVO toVO(FormEntity formEntity);

    public abstract FormFieldVO toFormFieldVO(FormEntity formEntity);

    public abstract FormEntity toEntity(FormVO formVO);
}
