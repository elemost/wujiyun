package com.wuji.service.converter;

import com.wuji.service.model.entity.FormInfoEntity;
import com.wuji.service.model.request.FormInfoCreateRequest;
import com.wuji.service.model.request.FormInfoUpdateRequest;
import com.wuji.service.model.vo.FormInfoVO;
import com.wuji.service.model.vo.TemplateFormInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormInfoConverter {
    public static final AbstractFormInfoConverter INSTANCE = Mappers.getMapper(AbstractFormInfoConverter.class);

    public abstract FormInfoEntity toEntity(FormInfoCreateRequest formInfoCreateRequest);

    public abstract FormInfoEntity toEntity(FormInfoUpdateRequest formInfoUpdateRequest);

    public abstract FormInfoVO toVO(FormInfoEntity formInfoEntity);


    public abstract FormInfoEntity toEntity(TemplateFormInfoVO templateFormInfoVO);

    public abstract FormInfoEntity toEntity(FormInfoVO formInfoVO);

    public abstract FormInfoCreateRequest toRequest(FormInfoEntity formInfoEntity);
}
