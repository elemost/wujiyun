package com.wuji.service.converter;

import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.entity.TemplateApplicationCategoryEntity;
import com.wuji.service.model.info.TemplateApplicationCategoryVO;
import com.wuji.service.model.request.ApplicationCategoryCreateRequest;
import com.wuji.service.model.request.ApplicationCategoryFormListCreateRequest;
import com.wuji.service.model.request.ApplicationCategoryUpdateRequest;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractApplicationCategoryConverter {
    public static final AbstractApplicationCategoryConverter INSTANCE =
            Mappers.getMapper(AbstractApplicationCategoryConverter.class);

    public abstract ApplicationCategoryEntity toEntity(
            ApplicationCategoryCreateRequest applicationCategoryCreateRequest);

    public abstract ApplicationCategoryEntity toEntity(
            ApplicationCategoryFormListCreateRequest applicationCategoryFormListCreateRequest);

    public abstract ApplicationCategoryEntity toEntity(
            ApplicationCategoryUpdateRequest applicationCategoryUpdateRequest);

    public abstract ApplicationCategoryVO toVO(ApplicationCategoryEntity applicationCategoryEntity);

    public abstract ApplicationCategoryCreateRequest toRequest(ApplicationCategoryEntity applicationCategoryEntity);

    public abstract ApplicationCategoryEntity toEntity(
            TemplateApplicationCategoryEntity templateApplicationCategoryEntity);

    public abstract ApplicationCategoryEntity toEntity(TemplateApplicationCategoryVO templateApplicationCategoryVO);

}
