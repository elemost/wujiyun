package com.wuji.open.converter;

import com.wuji.open.model.vo.FormFileOpenVO;
import com.wuji.service.model.vo.FormImgVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormFileOpenConverter {
    public static final AbstractFormFileOpenConverter INSTANCE = Mappers.getMapper(AbstractFormFileOpenConverter.class);

    public abstract FormFileOpenVO toVO(FormImgVO formImgVO);
}
