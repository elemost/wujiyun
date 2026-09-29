package com.wuji.admin.converter;

import com.wuji.admin.model.entity.ItemEntity;
import com.wuji.admin.model.vo.ItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractItemConverter {
    public static final AbstractItemConverter INSTANCE = Mappers.getMapper(AbstractItemConverter.class);

    public abstract ItemVO toVO(ItemEntity itemEntity);
}
