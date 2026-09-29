package com.wuji.service.converter;

import com.wuji.service.model.entity.FormPublicPublishEntity;
import com.wuji.service.model.request.FormPublicPublishSaveRequest;
import com.wuji.service.model.vo.FormPublicPublishVO;
import com.wuji.service.model.vo.TemplateFormPublicPublishVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormPublicPublishConverter {
    public static final AbstractFormPublicPublishConverter INSTANCE =
            Mappers.getMapper(AbstractFormPublicPublishConverter.class);

    public abstract FormPublicPublishEntity toEntity(FormPublicPublishSaveRequest formPublicPublishSaveRequest);

    public abstract FormPublicPublishVO toVO(FormPublicPublishEntity formPublicPublishEntity);

    public abstract FormPublicPublishEntity toEntity(TemplateFormPublicPublishVO templateFormPublicPublishVO);

    public abstract FormPublicPublishEntity toEntity(FormPublicPublishVO formPublicPublishVO);

}
