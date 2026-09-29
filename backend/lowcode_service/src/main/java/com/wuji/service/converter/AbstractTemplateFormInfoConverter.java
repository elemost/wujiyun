package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormInfoEntity;
import com.wuji.service.model.vo.FormInfoVO;
import com.wuji.service.model.vo.TemplateFormInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormInfoConverter {
    public static final AbstractTemplateFormInfoConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormInfoConverter.class);

    public abstract TemplateFormInfoEntity toEntity(FormInfoVO formInfoVO);

    public abstract TemplateFormInfoVO toVO(TemplateFormInfoEntity templateFormInfoEntity);
}
