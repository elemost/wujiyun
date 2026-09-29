package com.wuji.service.converter;

import com.wuji.service.model.entity.ApplicationUserSortEntity;
import com.wuji.service.model.vo.ApplicationUserSortVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractApplicationUserSortConverter {
    public static final AbstractApplicationUserSortConverter INSTANCE =
            Mappers.getMapper(AbstractApplicationUserSortConverter.class);

    public abstract ApplicationUserSortVO toVO(ApplicationUserSortEntity applicationUserSortEntity);
}
