package com.wuji.admin.converter;

import com.wuji.admin.model.entity.MenuEntity;
import com.wuji.admin.model.request.MenuCreateRequest;
import com.wuji.admin.model.request.MenuUpdateRequest;
import com.wuji.admin.model.vo.MenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractMenuConverter {
    public static final AbstractMenuConverter INSTANCE = Mappers.getMapper(AbstractMenuConverter.class);

    public abstract MenuVO toVO(MenuEntity menuEntity);

    public abstract MenuEntity toEntity(MenuCreateRequest menuCreateRequest);

    public abstract MenuEntity toEntity(MenuUpdateRequest menuUpdateRequest);
}
