package com.wuji.platform.converter;

import com.wuji.platform.model.entity.SecretLogEntity;
import com.wuji.platform.model.request.SecretLogSaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractSecretLogConverter {
    public static final AbstractSecretLogConverter INSTANCE = Mappers.getMapper(AbstractSecretLogConverter.class);

    public abstract SecretLogEntity toEntity (SecretLogSaveRequest secretLogSaveRequest);
}
