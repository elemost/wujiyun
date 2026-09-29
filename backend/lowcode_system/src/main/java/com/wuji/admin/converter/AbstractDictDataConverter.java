package com.wuji.admin.converter;

import com.wuji.admin.model.entity.DictDataEntity;
import com.wuji.admin.model.vo.DictDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractDictDataConverter {
    public static final AbstractDictDataConverter INSTANCE = Mappers.getMapper(AbstractDictDataConverter.class);

    public abstract DictDataVO toVO(DictDataEntity dictDataEntity);
}
