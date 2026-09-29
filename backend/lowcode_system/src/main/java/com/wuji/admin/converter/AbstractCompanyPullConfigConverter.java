package com.wuji.admin.converter;

import com.wuji.admin.model.entity.CompanyPullConfigEntity;
import com.wuji.common.model.vo.CompanyPullConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractCompanyPullConfigConverter {
    public static final AbstractCompanyPullConfigConverter INSTANCE =
            Mappers.getMapper(AbstractCompanyPullConfigConverter.class);

    public abstract CompanyPullConfigVO toVO(CompanyPullConfigEntity companyPullConfigEntity);

}
