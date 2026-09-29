package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormExtraFunctionRelationEntity;
import com.wuji.service.model.vo.FormExtraFunctionRelationVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionRelationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormExtraFunctionRelationConverter {
    public static final AbstractTemplateFormExtraFunctionRelationConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormExtraFunctionRelationConverter.class);

    @Mapping(target = "id", ignore = true)
    public abstract TemplateFormExtraFunctionRelationEntity toEntity(
            FormExtraFunctionRelationVO formExtraFunctionRelationVO);

    public abstract TemplateFormExtraFunctionRelationVO toVO(
            TemplateFormExtraFunctionRelationEntity templateFormExtraFunctionRelationEntity);
}
