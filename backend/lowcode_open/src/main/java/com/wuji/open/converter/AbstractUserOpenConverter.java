package com.wuji.open.converter;

import com.wuji.common.model.vo.UserVO;
import com.wuji.open.model.vo.UserOpenVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractUserOpenConverter {
    public static final AbstractUserOpenConverter INSTANCE = Mappers.getMapper(AbstractUserOpenConverter.class);

    public abstract UserOpenVO toVO(UserVO userVO);
}
