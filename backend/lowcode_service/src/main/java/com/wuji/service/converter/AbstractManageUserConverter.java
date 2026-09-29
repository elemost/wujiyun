package com.wuji.service.converter;

import com.wuji.service.model.entity.ManageUserEntity;
import com.wuji.service.model.vo.ManageUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractManageUserConverter {

    public static final AbstractManageUserConverter INSTANCE = Mappers.getMapper(AbstractManageUserConverter.class);

    public abstract ManageUserVO toVO(ManageUserEntity manageUserEntity);
}
