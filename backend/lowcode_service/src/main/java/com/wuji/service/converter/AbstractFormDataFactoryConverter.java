package com.wuji.service.converter;

import com.wuji.service.model.entity.FormDataFactoryEntity;
import com.wuji.service.model.request.FormDataFactoryCreateRequest;
import com.wuji.service.model.request.FormDataFactoryUpdateRequest;
import com.wuji.service.model.vo.FormDataFactoryVO;
import com.wuji.service.model.vo.TemplateFormDataFactoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormDataFactoryConverter {

    public static final AbstractFormDataFactoryConverter INSTANCE =
            Mappers.getMapper(AbstractFormDataFactoryConverter.class);

    public abstract FormDataFactoryEntity toEntity(FormDataFactoryCreateRequest formDataFactoryCreateRequest);

    public abstract FormDataFactoryEntity toEntity(FormDataFactoryUpdateRequest formDataFactoryUpdateRequest);

    public abstract FormDataFactoryVO toVO(FormDataFactoryEntity formDataFactoryEntity);

    public abstract FormDataFactoryEntity toEntity(TemplateFormDataFactoryVO templateFormDataFactoryVO);

}
