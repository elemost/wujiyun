package com.wuji.service.converter;

import com.wuji.service.model.entity.FormExtraFunctionRelationEntity;
import com.wuji.service.model.vo.FormExtraFunctionRelationVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionRelationVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormExtraFunctionRelationConverter {
    public static final AbstractFormExtraFunctionRelationConverter INSTANCE =
            Mappers.getMapper(AbstractFormExtraFunctionRelationConverter.class);

    public abstract FormExtraFunctionRelationEntity toEntity(
            TemplateFormExtraFunctionRelationVO templateFormExtraFunctionRelationVO);


    public abstract FormExtraFunctionRelationVO toVO(
            FormExtraFunctionRelationEntity formExtraFunctionRelationEntity);

}
