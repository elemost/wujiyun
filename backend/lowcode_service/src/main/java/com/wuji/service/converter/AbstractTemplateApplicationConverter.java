package com.wuji.service.converter;

import com.wuji.service.model.domain.TemplateApplicationDomain;
import com.wuji.service.model.entity.TemplateApplicationEntity;
import com.wuji.service.model.request.TemplateApplicationRequest;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.TemplateApplicationVO;
import com.wuji.systemapi.client.user.model.TemplateApplicationOpenRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateApplicationConverter {
    public static final AbstractTemplateApplicationConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateApplicationConverter.class);

    public abstract TemplateApplicationEntity toEntity(ApplicationVO applicationVO);

    public abstract TemplateApplicationVO toVO(TemplateApplicationEntity templateApplicationEntity);

    public abstract TemplateApplicationVO toVO(TemplateApplicationDomain templateApplicationDomain);

    public abstract TemplateApplicationOpenRequest toRequest(TemplateApplicationRequest templateApplicationRequest);

}
