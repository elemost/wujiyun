package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateApplicationImgEntity;
import com.wuji.service.model.request.TemplateApplicationImgRequest;
import com.wuji.service.model.vo.TemplateApplicationImgVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateApplicationImgConverter {
    public static final AbstractTemplateApplicationImgConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateApplicationImgConverter.class);


    public abstract TemplateApplicationImgVO toVO(TemplateApplicationImgEntity templateApplicationImgEntity);

    public abstract TemplateApplicationImgEntity toEntity(TemplateApplicationImgRequest templateApplicationImgRequest);


}
