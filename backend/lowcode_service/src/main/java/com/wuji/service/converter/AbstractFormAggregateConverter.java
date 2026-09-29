package com.wuji.service.converter;

import com.wuji.service.model.entity.FormAggregateEntity;
import com.wuji.service.model.request.FormAggregateCreateRequest;
import com.wuji.service.model.request.FormAggregateUpdateRequest;
import com.wuji.service.model.vo.FormAggregateVO;
import com.wuji.service.model.vo.TemplateFormAggregateVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormAggregateConverter {

    public static final AbstractFormAggregateConverter INSTANCE =
            Mappers.getMapper(AbstractFormAggregateConverter.class);

    public abstract FormAggregateEntity toEntity(FormAggregateCreateRequest formAggregateCreateRequest);

    public abstract FormAggregateEntity toEntity(FormAggregateUpdateRequest formAggregateUpdateRequest);

    public abstract FormAggregateVO toVO(FormAggregateEntity formAggregateEntity);

    public abstract FormAggregateEntity toEntity(TemplateFormAggregateVO templateFormAggregateVO);

    public abstract FormAggregateEntity toEntity(FormAggregateVO formAggregateVO);

}
