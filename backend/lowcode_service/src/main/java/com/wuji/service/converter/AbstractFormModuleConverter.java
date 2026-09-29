package com.wuji.service.converter;

import com.wuji.service.model.entity.FormModuleEntity;
import com.wuji.service.model.request.FormModuleInsertRequest;
import com.wuji.service.model.request.FormModuleUpdateRequest;
import com.wuji.service.model.vo.FormModuleVO;
import com.wuji.service.model.vo.TemplateFormModuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormModuleConverter {
    public static final AbstractFormModuleConverter INSTANCE = Mappers.getMapper(AbstractFormModuleConverter.class);

    public abstract FormModuleEntity toEntity(FormModuleInsertRequest formModuleInsertRequest);

    public abstract FormModuleEntity toEntity(FormModuleUpdateRequest formModuleUpdateRequest);

    public abstract FormModuleVO toVO(FormModuleEntity formModuleEntity);

    public abstract FormModuleEntity toEntity(TemplateFormModuleVO templateFormModuleVO);

    public abstract FormModuleEntity toEntity(FormModuleVO formModuleVO);


}
