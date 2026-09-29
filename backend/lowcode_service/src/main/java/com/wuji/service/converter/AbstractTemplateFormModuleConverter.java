package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormModuleEntity;
import com.wuji.service.model.vo.FormModuleVO;
import com.wuji.service.model.vo.TemplateFormModuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormModuleConverter {
    public static final AbstractTemplateFormModuleConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormModuleConverter.class);

    public abstract TemplateFormModuleEntity toEntity(FormModuleVO formModuleVO);

    public abstract TemplateFormModuleVO toVO(TemplateFormModuleEntity templateFormModuleEntity);
}
