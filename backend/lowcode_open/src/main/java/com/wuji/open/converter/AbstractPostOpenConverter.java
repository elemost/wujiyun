package com.wuji.open.converter;

import com.wuji.admin.model.request.PostCreateRequest;
import com.wuji.admin.model.request.PostSelectRequest;
import com.wuji.admin.model.request.PostUpdateRequest;
import com.wuji.common.model.vo.PostVO;
import com.wuji.open.model.request.PostCreateOpenRequest;
import com.wuji.open.model.request.PostSelectOpenRequest;
import com.wuji.open.model.request.PostUpdateOpenRequest;
import com.wuji.open.model.vo.PostOpenVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractPostOpenConverter {
    public static final AbstractPostOpenConverter INSTANCE = Mappers.getMapper(AbstractPostOpenConverter.class);

    public abstract PostCreateRequest toRequest(PostCreateOpenRequest postCreateRequest);

    public abstract PostUpdateRequest toRequest(PostUpdateOpenRequest postUpdateOpenRequest);

    public abstract PostSelectRequest toRequest(PostSelectOpenRequest postSelectOpenRequest);

    public abstract PostOpenVO toVO(PostVO postVO);

}
