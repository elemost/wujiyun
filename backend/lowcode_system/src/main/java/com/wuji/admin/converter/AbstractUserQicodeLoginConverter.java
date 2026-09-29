package com.wuji.admin.converter;

import com.wuji.admin.model.entity.UserQicodeLoginEntity;
import com.wuji.admin.model.vo.UserQicodeLoginVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractUserQicodeLoginConverter {

    public static final AbstractUserQicodeLoginConverter INSTANCE =
            Mappers.getMapper(AbstractUserQicodeLoginConverter.class);

    public abstract UserQicodeLoginVO toVO(UserQicodeLoginEntity userQicodeLoginEntity);
}
