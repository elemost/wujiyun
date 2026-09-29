package com.wuji.service.converter;

import com.wuji.service.model.entity.FormPrivilegeUserEntity;
import com.wuji.service.model.request.FormPrivilegeUserRequest;
import com.wuji.service.model.vo.FormPrivilegeUserVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormPrivilegeUserConverter {
    public static final AbstractFormPrivilegeUserConverter INSTANCE =
            Mappers.getMapper(AbstractFormPrivilegeUserConverter.class);

    public abstract FormPrivilegeUserEntity toEntity(FormPrivilegeUserRequest formPrivilegeUserRequest);

    public abstract FormPrivilegeUserEntity toEntity(TemplateFormPrivilegeUserVO templateFormPrivilegeUserVO);

    public abstract FormPrivilegeUserVO toVO(FormPrivilegeUserEntity formPrivilegeUserEntity);
}
