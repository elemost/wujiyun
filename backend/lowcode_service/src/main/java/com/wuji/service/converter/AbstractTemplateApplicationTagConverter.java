package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateApplicationTagEntity;
import com.wuji.service.model.request.TemplateApplicationTagRequest;
import com.wuji.service.model.vo.TemplateApplicationTagVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateApplicationTagConverter {

    public static final AbstractTemplateApplicationTagConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateApplicationTagConverter.class);

    public abstract TemplateApplicationTagVO toVO(TemplateApplicationTagEntity templateApplicationTagEntity);

    public abstract TemplateApplicationTagEntity toEntity(TemplateApplicationTagRequest templateApplicationTagRequest);
}
