package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormDataStreamEntity;
import com.wuji.service.model.vo.FormDataStreamPublishVO;
import com.wuji.service.model.vo.TemplateFormDataStreamVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormDataStreamConverter {
    public static final AbstractTemplateFormDataStreamConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormDataStreamConverter.class);

    public abstract TemplateFormDataStreamEntity toEntity(FormDataStreamPublishVO formDataStreamPublishVO);

    public abstract TemplateFormDataStreamVO toVO(TemplateFormDataStreamEntity templateFormDataStreamEntity);

}
