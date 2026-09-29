package com.wuji.open.converter;

import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.open.model.vo.DeptOpenTreeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractDeptOpenConverter {
    public static final AbstractDeptOpenConverter INSTANCE = Mappers.getMapper(AbstractDeptOpenConverter.class);

    public abstract DeptOpenTreeVO toVO(DepartmentVO departmentVO);
}
