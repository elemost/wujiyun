package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormPrivilegeEntity;
import com.wuji.service.model.vo.FormPrivilegeDetailVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormPrivilegeConverter {
    public static final AbstractTemplateFormPrivilegeConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormPrivilegeConverter.class);

    public abstract TemplateFormPrivilegeEntity toEntity(FormPrivilegeDetailVO formPrivilegeDetailVO);

    public abstract TemplateFormPrivilegeVO toVO(TemplateFormPrivilegeEntity templateFormPrivilegeEntity);
}
