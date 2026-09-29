package com.wuji.service.converter;

import com.wuji.service.model.entity.FormModelEntity;
import com.wuji.service.model.request.FormModelDesignerRequest;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormModelConverter {
    public static final AbstractFormModelConverter INSTANCE = Mappers.getMapper(AbstractFormModelConverter.class);

    public abstract FormModelVO toVO(FormModelEntity formModelEntity);

    public abstract FormModelDesignerDomain toDomain(FormModelDesignerRequest formModelDesignerRequest);
}
