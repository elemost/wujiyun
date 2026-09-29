package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormExtraFunctionEntity;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormExtraFunctionConverter {
    public static final AbstractTemplateFormExtraFunctionConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormExtraFunctionConverter.class);

    public abstract TemplateFormExtraFunctionEntity toEntity(FormExtraFunctionVO formExtraFunctionVO);

    public abstract TemplateFormExtraFunctionVO toVO(TemplateFormExtraFunctionEntity templateFormExtraFunctionEntity);
}
