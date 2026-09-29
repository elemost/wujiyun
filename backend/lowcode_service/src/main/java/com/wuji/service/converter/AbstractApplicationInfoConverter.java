package com.wuji.service.converter;


import com.wuji.service.model.entity.ApplicationInfoEntity;
import com.wuji.service.model.request.ApplicationInfoRequest;
import com.wuji.service.model.vo.ApplicationInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractApplicationInfoConverter {
    public static final AbstractApplicationInfoConverter INSTANCE =
            Mappers.getMapper(AbstractApplicationInfoConverter.class);

    public abstract ApplicationInfoEntity toEntity(ApplicationInfoRequest applicationInfoRequest);


    public abstract ApplicationInfoVO toVO(ApplicationInfoEntity applicationInfoEntity);
}
