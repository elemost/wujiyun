package com.wuji.admin.converter;

import com.wuji.admin.model.entity.ThirdUserEntity;
import com.wuji.admin.model.vo.ThirdUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractThirdUserConverter {
    public static final AbstractThirdUserConverter INSTANCE = Mappers.getMapper(AbstractThirdUserConverter.class);

    public abstract ThirdUserVO toVO(ThirdUserEntity thirdUserEntity);
}
