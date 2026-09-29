package com.wuji.platform.converter;


import com.wuji.platform.model.entity.SyncMappingEntity;
import com.wuji.platform.model.request.SyncMappingSaveRequest;
import com.wuji.platform.model.vo.SyncMappingVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractSyncMappingConverter {
    public static final AbstractSyncMappingConverter INSTANCE = Mappers.getMapper(AbstractSyncMappingConverter.class);

    public abstract SyncMappingVO toVO(SyncMappingEntity syncMappingEntity);

    public abstract SyncMappingEntity toEntity(SyncMappingSaveRequest syncMappingSaveRequest);
}
