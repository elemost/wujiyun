package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormPublicPublishEntity;
import com.wuji.service.model.vo.FormPublicPublishVO;
import com.wuji.service.model.vo.TemplateFormPublicPublishVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormPublicPublishConverter {
    public static final AbstractTemplateFormPublicPublishConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormPublicPublishConverter.class);

    public abstract TemplateFormPublicPublishEntity toEntity(FormPublicPublishVO formPublicPublishVO);

    public abstract TemplateFormPublicPublishVO toVO(TemplateFormPublicPublishEntity templateFormPublicPublishEntity);
}
