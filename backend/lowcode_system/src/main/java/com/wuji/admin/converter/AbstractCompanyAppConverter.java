package com.wuji.admin.converter;

import com.wuji.admin.model.entity.CompanyAppEntity;
import com.wuji.admin.model.vo.CompanyAppVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractCompanyAppConverter {
    public static final AbstractCompanyAppConverter INSTANCE = Mappers.getMapper(AbstractCompanyAppConverter.class);

    public abstract CompanyAppVO toVO(CompanyAppEntity companyAppEntity);
}
