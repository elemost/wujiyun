package com.wuji.admin.converter;

import com.wuji.admin.model.entity.UserInfoEntity;
import com.wuji.admin.model.request.UserInfoSaveRequest;
import com.wuji.common.model.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public abstract class AbstractUserInfoConverter {
    public static final AbstractUserInfoConverter INSTANCE = Mappers.getMapper(AbstractUserInfoConverter.class);

    public abstract UserInfoEntity toEntity(UserInfoSaveRequest userInfoSaveRequest);

    public abstract UserInfoVO toVO(UserInfoEntity userInfoEntity);

}
