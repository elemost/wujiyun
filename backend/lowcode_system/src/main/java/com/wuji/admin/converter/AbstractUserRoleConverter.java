package com.wuji.admin.converter;

import com.wuji.admin.model.entity.UserRoleEntity;
import com.wuji.admin.model.vo.RoleVO;
import com.wuji.common.model.vo.UserRoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractUserRoleConverter {

    public static final AbstractUserRoleConverter INSTANCE = Mappers.getMapper(AbstractUserRoleConverter.class);


    public abstract UserRoleVO toVO(RoleVO roleVO);

    public abstract UserRoleVO toVO(UserRoleEntity userRoleEntity);


}
