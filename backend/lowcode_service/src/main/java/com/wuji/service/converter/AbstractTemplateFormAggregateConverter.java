package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormAggregateEntity;
import com.wuji.service.model.vo.FormAggregateVO;
import com.wuji.service.model.vo.TemplateFormAggregateVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormAggregateConverter {
    public static final AbstractTemplateFormAggregateConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormAggregateConverter.class);
    public abstract TemplateFormAggregateEntity toEntity(FormAggregateVO formAggregateVO);

    public abstract TemplateFormAggregateVO toVO(TemplateFormAggregateEntity templateFormAggregateEntity);
}
