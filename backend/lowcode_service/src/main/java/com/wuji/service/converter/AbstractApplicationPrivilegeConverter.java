package com.wuji.service.converter;

import com.wuji.admin.model.info.UserScope;
import com.wuji.service.model.entity.ApplicationPrivilegeEntity;
import com.wuji.service.model.request.ApplicationPrivilegeSaveRequest;
import com.wuji.service.model.vo.ApplicationPrivilegeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractApplicationPrivilegeConverter {
    public static final AbstractApplicationPrivilegeConverter INSTANCE =
            Mappers.getMapper(AbstractApplicationPrivilegeConverter.class);

    public abstract ApplicationPrivilegeEntity toEntity(
            ApplicationPrivilegeSaveRequest applicationPrivilegeSaveRequest);

    public abstract ApplicationPrivilegeVO toVO(ApplicationPrivilegeEntity applicationPrivilegeEntity);

    public abstract UserScope toScope(ApplicationPrivilegeEntity applicationPrivilegeEntity);

}
