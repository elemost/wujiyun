package com.wuji.service.converter;

import com.wuji.common.model.vo.DataFactoryReturnFieldCommonVO;
import com.wuji.service.model.info.FormConfigCommon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormConfigCommonConverter {

    public static final AbstractFormConfigCommonConverter INSTANCE =
            Mappers.getMapper(AbstractFormConfigCommonConverter.class);


    @Mapping(source = "aliasName", target = "id")
    @Mapping(source = "aliasName", target = "name")
    @Mapping(source = "fieldType", target = "type")
    @Mapping(source = "label", target = "label")
    public abstract FormConfigCommon toConfig(DataFactoryReturnFieldCommonVO dataFactoryReturnFieldCommonVO);


}
