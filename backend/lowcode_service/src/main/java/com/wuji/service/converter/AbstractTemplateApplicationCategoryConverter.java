package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateApplicationCategoryEntity;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public abstract class AbstractTemplateApplicationCategoryConverter {
    public static final AbstractTemplateApplicationCategoryConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateApplicationCategoryConverter.class);

    public abstract TemplateApplicationCategoryEntity toEntity(ApplicationCategoryVO applicationCategoryVO);
}
