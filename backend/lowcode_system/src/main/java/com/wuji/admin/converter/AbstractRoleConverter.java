package com.wuji.admin.converter;

import com.wuji.admin.model.entity.RoleEntity;
import com.wuji.admin.model.request.RoleCreateRequest;
import com.wuji.admin.model.request.RoleUpdateRequest;
import com.wuji.admin.model.vo.RoleDetailVO;
import com.wuji.admin.model.vo.RoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractRoleConverter {
    public static final AbstractRoleConverter INSTANCE = Mappers.getMapper(AbstractRoleConverter.class);

    public abstract RoleEntity toEntity(RoleCreateRequest roleCreateRequest);

    public abstract RoleEntity toEntity(RoleUpdateRequest roleUpdateRequest);

    public abstract RoleVO toVO(RoleEntity roleEntity);

    public abstract RoleDetailVO toDetailVO(RoleEntity roleEntity);

}
