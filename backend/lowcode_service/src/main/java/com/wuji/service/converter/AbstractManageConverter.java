package com.wuji.service.converter;

import com.wuji.service.model.entity.ManageEntity;
import com.wuji.service.model.request.ManageCreateRequest;
import com.wuji.service.model.request.ManageUpdateRequest;
import com.wuji.service.model.vo.ManageVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractManageConverter {

    public static final AbstractManageConverter INSTANCE = Mappers.getMapper(AbstractManageConverter.class);

    public abstract ManageEntity toEntity(ManageCreateRequest manageCreateRequest);

    public abstract ManageEntity toEntity(ManageUpdateRequest manageUpdateRequest);

    public abstract ManageVO toVO(ManageEntity manageEntity);

}
