package com.wuji.service.converter;

import com.wuji.service.model.entity.FormEntity;
import com.wuji.service.model.entity.TemplateFormEntity;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.TemplateFormVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public abstract class AbstractTemplateFormConverter {
    public static final AbstractTemplateFormConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormConverter.class);

    public abstract TemplateFormEntity toEntity(FormVO formVO);

    public abstract FormEntity toEntity(TemplateFormVO templateFormVO);

    public abstract TemplateFormVO toVO(TemplateFormEntity templateFormEntity);
}
