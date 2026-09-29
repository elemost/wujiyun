package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormDataFactoryEntity;
import com.wuji.service.model.vo.FormDataFactoryPublishVO;
import com.wuji.service.model.vo.TemplateFormDataFactoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormDataFactoryConverter {
    public static final AbstractTemplateFormDataFactoryConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormDataFactoryConverter.class);

    public abstract TemplateFormDataFactoryEntity toEntity(FormDataFactoryPublishVO formDataFactoryPublishVO);

    public abstract TemplateFormDataFactoryVO toVO(TemplateFormDataFactoryEntity templateFormDataFactoryEntity);

}
