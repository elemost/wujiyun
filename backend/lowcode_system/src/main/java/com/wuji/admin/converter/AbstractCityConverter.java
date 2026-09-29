package com.wuji.admin.converter;

import com.wuji.admin.model.entity.CityEntity;
import com.wuji.admin.model.vo.CityVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractCityConverter {

    public static final AbstractCityConverter INSTANCE = Mappers.getMapper(AbstractCityConverter.class);

    @Mapping(source = "cityName", target = "label")
    @Mapping(source = "code", target = "value")
    public abstract CityVO toVO(CityEntity cityEntity);
}
