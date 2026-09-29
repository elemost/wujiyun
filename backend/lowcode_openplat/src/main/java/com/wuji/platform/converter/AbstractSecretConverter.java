package com.wuji.platform.converter;


import com.wuji.platform.model.entity.SecretEntity;
import com.wuji.platform.model.vo.SecretVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractSecretConverter {

    public static final AbstractSecretConverter INSTANCE = Mappers.getMapper(AbstractSecretConverter.class);

    public abstract SecretVO toVO(SecretEntity secretEntity);
}
