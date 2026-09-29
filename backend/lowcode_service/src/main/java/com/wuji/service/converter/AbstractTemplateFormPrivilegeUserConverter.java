package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormPrivilegeUserEntity;
import com.wuji.service.model.vo.FormPrivilegeUserVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormPrivilegeUserConverter {
    public static final AbstractTemplateFormPrivilegeUserConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormPrivilegeUserConverter.class);

    public abstract TemplateFormPrivilegeUserEntity toEntity(FormPrivilegeUserVO formPrivilegeUserVO);

    public abstract TemplateFormPrivilegeUserVO toVO(TemplateFormPrivilegeUserEntity templateFormPrivilegeUserEntity);
}
