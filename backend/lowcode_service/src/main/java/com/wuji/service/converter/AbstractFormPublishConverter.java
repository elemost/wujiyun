package com.wuji.service.converter;

import com.wuji.service.model.entity.FormEntity;
import com.wuji.service.model.entity.FormPublishEntity;
import com.wuji.service.model.request.FormPublishPublishRequest;
import com.wuji.service.model.vo.FormPublishVO;
import com.wuji.service.model.vo.FormVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormPublishConverter {
    public static final AbstractFormPublishConverter INSTANCE = Mappers.getMapper(AbstractFormPublishConverter.class);

    public abstract FormPublishEntity toEntity(FormPublishPublishRequest formPublishPublishRequest);

    public abstract FormPublishPublishRequest toRequest(FormEntity formEntity);

    public abstract FormPublishPublishRequest toRequest(FormVO formVO);

    public abstract FormPublishVO toVO(FormPublishEntity formPublishEntity);
}
