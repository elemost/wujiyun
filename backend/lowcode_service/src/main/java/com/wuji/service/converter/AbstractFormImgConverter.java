package com.wuji.service.converter;

import com.wuji.service.model.entity.FormImgEntity;
import com.wuji.service.model.request.FormImgRequest;
import com.wuji.service.model.vo.FileVO;
import com.wuji.service.model.vo.FormImgVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormImgConverter {
    public static final AbstractFormImgConverter INSTANCE = Mappers.getMapper(AbstractFormImgConverter.class);

    public abstract FormImgEntity toEntity(FormImgRequest formImgRequest);


    public abstract FormImgVO toVO(FormImgEntity formImgEntity);

    public abstract FormImgVO toVO(FileVO fileVO);
}
