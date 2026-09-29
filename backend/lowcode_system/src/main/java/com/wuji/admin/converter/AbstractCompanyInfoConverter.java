package com.wuji.admin.converter;

import com.wuji.admin.model.entity.CompanyInfoEntity;
import com.wuji.admin.model.request.CompanyInfoSaveRequest;
import com.wuji.admin.model.vo.CompanyInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractCompanyInfoConverter {
    public static final AbstractCompanyInfoConverter INSTANCE = Mappers.getMapper(AbstractCompanyInfoConverter.class);

    public abstract CompanyInfoVO toVO(CompanyInfoEntity companyInfoEntity);

    public abstract CompanyInfoEntity toEntity(CompanyInfoSaveRequest companyInfoSaveRequest);

}
