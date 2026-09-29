package com.wuji.admin.converter;

import com.wuji.admin.model.entity.PostEntity;
import com.wuji.admin.model.request.PostCreateRequest;
import com.wuji.admin.model.request.PostUpdateRequest;
import com.wuji.common.model.vo.PostVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public abstract class AbstractPostConverter {
    public static final AbstractPostConverter INSTANCE = Mappers.getMapper(AbstractPostConverter.class);

    public abstract PostEntity toEntity(PostCreateRequest postCreateRequest);

    public abstract PostEntity toEntity(PostUpdateRequest postUpdateRequest);

    public abstract PostVO toVO(PostEntity postEntity);
}
