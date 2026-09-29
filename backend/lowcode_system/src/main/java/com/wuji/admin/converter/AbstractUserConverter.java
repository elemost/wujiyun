package com.wuji.admin.converter;

import com.wuji.admin.model.domain.UserExcelDomain;
import com.wuji.admin.model.entity.UserEntity;
import com.wuji.admin.model.request.CorpCoopUserRequest;
import com.wuji.admin.model.request.UserCreateRequest;
import com.wuji.admin.model.request.UserInviteRequest;
import com.wuji.admin.model.request.UserUpdateRequest;
import com.wuji.admin.model.vo.UserReturnInfoVO;
import com.wuji.admin.model.vo.UserReturnVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.common.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractUserConverter {
    public static final AbstractUserConverter INSTANCE = Mappers.getMapper(AbstractUserConverter.class);

    public abstract UserVO toVO(UserEntity userEntity);

    public abstract UserReturnVO toReturnVO(UserEntity userEntity);

    public abstract UserReturnInfoVO toLoginVO(UserVO userVO);

    public abstract UserEntity toEntity(UserCreateRequest userCreateRequest);

    public abstract UserEntity toEntity(UserInviteRequest userInviteRequest);

    public abstract UserEntity toEntity(UserUpdateRequest userUpdateRequest);

    public abstract UserEntity toEntity(UserExcelDomain userExcelDomain);

    public abstract UserEntity toEntity(UserPullVO userDingVO);

    public abstract UserEntity toEntity(CorpCoopUserRequest corpCoopUserRequest);


}
