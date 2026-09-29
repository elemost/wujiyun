package com.wuji.admin.converter;


import com.wuji.admin.model.entity.CompanyEntity;
import com.wuji.admin.model.request.CompanyPullConfigSaveRequest;
import com.wuji.admin.model.request.CompanyRequest;
import com.wuji.admin.model.request.CompanySaveRequest;
import com.wuji.admin.model.vo.CompanyVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractCompanyConverter {
    public static final AbstractCompanyConverter INSTANCE = Mappers.getMapper(AbstractCompanyConverter.class);

    @Mapping(source = "id", target = "companyId")
    public abstract CompanyVO toVO(CompanyEntity companyEntity);

    public abstract CompanyEntity toEntity(CompanyPullConfigSaveRequest companyPullConfigSaveRequest);

    public abstract CompanyEntity toEntity(CompanyRequest companyRequest);

    public abstract CompanyEntity toEntity(CompanySaveRequest companySaveRequest);
}
