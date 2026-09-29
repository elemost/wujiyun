package com.wuji.service.converter;

import com.wuji.service.model.domain.ApplicationDomain;
import com.wuji.service.model.entity.ApplicationEntity;
import com.wuji.service.model.entity.TemplateApplicationEntity;
import com.wuji.service.model.request.ApplicationCreateRequest;
import com.wuji.service.model.request.ApplicationUpdateRequest;
import com.wuji.service.model.vo.ApplicationFlowableVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.TemplateApplicationVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractApplicationConverter {
    public static final AbstractApplicationConverter INSTANCE = Mappers.getMapper(AbstractApplicationConverter.class);

    public abstract ApplicationEntity toEntity(ApplicationCreateRequest applicationCreateRequest);

    public abstract ApplicationEntity toEntity(ApplicationUpdateRequest applicationUpdateRequest);

    public abstract ApplicationVO toVO(ApplicationEntity applicationEntity);

    public abstract ApplicationVO toVO(ApplicationDomain applicationDomain);

    public abstract ApplicationEntity toEntity(TemplateApplicationEntity templateApplicationEntity);

    public abstract ApplicationFlowableVO toFlowableVO(ApplicationDomain applicationDomain);

    public abstract ApplicationFlowableVO toFlowableVO(ApplicationEntity applicationEntity);

    public abstract ApplicationEntity toEntity(TemplateApplicationVO templateApplicationVO);
}
