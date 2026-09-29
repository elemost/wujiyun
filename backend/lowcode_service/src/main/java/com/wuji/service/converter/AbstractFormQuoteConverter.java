package com.wuji.service.converter;

import com.wuji.service.model.entity.FormQuoteEntity;
import com.wuji.service.model.request.FormQuoteSaveInfoRequest;
import com.wuji.service.model.vo.FormQuoteInfoVO;
import com.wuji.service.model.vo.TemplateFormQuoteVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormQuoteConverter {
    public static final AbstractFormQuoteConverter INSTANCE = Mappers.getMapper(AbstractFormQuoteConverter.class);

    public abstract FormQuoteEntity toEntity(FormQuoteSaveInfoRequest formQuoteSaveRequest);

    public abstract FormQuoteInfoVO toVO(FormQuoteEntity formQuoteEntity);

    public abstract FormQuoteEntity toEntity(TemplateFormQuoteVO templateFormQuoteVO);
}
