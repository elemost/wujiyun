package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormQuoteEntity;
import com.wuji.service.model.vo.FormQuoteInfoVO;
import com.wuji.service.model.vo.TemplateFormQuoteVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormQuoteConverter {
    public static final AbstractTemplateFormQuoteConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormQuoteConverter.class);

    public abstract TemplateFormQuoteEntity toEntity(FormQuoteInfoVO formQuoteInfoVO);


    public abstract TemplateFormQuoteVO toVO(TemplateFormQuoteEntity templateFormQuoteEntity);
}
