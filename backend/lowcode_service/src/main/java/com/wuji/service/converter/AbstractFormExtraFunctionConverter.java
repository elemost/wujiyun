package com.wuji.service.converter;

import com.wuji.service.model.entity.FormExtraFunctionEntity;
import com.wuji.service.model.request.FormExtraFunctionCreateRequest;
import com.wuji.service.model.request.FormExtraFunctionUpdateRequest;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormExtraFunctionConverter {
    public static final AbstractFormExtraFunctionConverter INSTANCE =
            Mappers.getMapper(AbstractFormExtraFunctionConverter.class);

    public abstract FormExtraFunctionEntity toEntity(FormExtraFunctionCreateRequest formExtraFunctionCreateRequest);

    public abstract FormExtraFunctionEntity toEntity(FormExtraFunctionUpdateRequest formExtraFunctionUpdateRequest);

    public abstract FormExtraFunctionVO toVO(FormExtraFunctionEntity formExtraFunctionEntity);

    public abstract FormExtraFunctionEntity toEntity(TemplateFormExtraFunctionVO templateFormExtraFunctionVO);
}
