package com.wuji.admin.converter;

import com.wuji.admin.model.domain.UserExcelDomain;
import com.wuji.admin.model.entity.UserCompanyEntity;
import com.wuji.admin.model.request.UserCompanySaveRequest;
import com.wuji.admin.model.request.UserCreateRequest;
import com.wuji.admin.model.request.UserUpdateRequest;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractUserCompanyConverter {
    public static final AbstractUserCompanyConverter INSTANCE = Mappers.getMapper(AbstractUserCompanyConverter.class);

    public abstract UserCompanyEntity toEntity(UserCompanySaveRequest userCompanySaveRequest);

    public abstract UserCompanySaveRequest toRequest(UserPullVO userPullVO);

    public abstract UserCompanySaveRequest toRequest(UserExcelDomain userExcelDomain);

    public abstract UserCompanySaveRequest toRequest(UserUpdateRequest userUpdateRequest);

    public abstract UserCompanySaveRequest toRequest(UserCreateRequest userCreateRequest);

    public abstract UserCompanyVO toVO(UserCompanyEntity userCompanyEntity);

}
