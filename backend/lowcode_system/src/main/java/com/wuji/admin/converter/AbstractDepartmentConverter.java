package com.wuji.admin.converter;

import com.wuji.admin.model.entity.DepartmentEntity;
import com.wuji.admin.model.request.DepartmentCreateRequest;
import com.wuji.admin.model.request.DepartmentSaveOrUpdateRequest;
import com.wuji.admin.model.request.DepartmentUpdateRequest;
import com.wuji.common.model.vo.DepartmentVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractDepartmentConverter {
    public static final AbstractDepartmentConverter INSTANCE = Mappers.getMapper(AbstractDepartmentConverter.class);

    public abstract DepartmentVO toVO(DepartmentEntity departmentEntity);

    public abstract DepartmentEntity toEntity(DepartmentCreateRequest departmentCreateRequest);

    public abstract DepartmentEntity toEntity(DepartmentUpdateRequest departmentUpdateRequest);


    public abstract DepartmentEntity toEntity(DepartmentSaveOrUpdateRequest departmentSaveOrUpdateRequest);


}
