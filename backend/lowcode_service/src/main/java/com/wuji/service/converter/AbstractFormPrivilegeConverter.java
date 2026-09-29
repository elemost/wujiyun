package com.wuji.service.converter;

import com.wuji.service.model.entity.FormPrivilegeEntity;
import com.wuji.service.model.request.FormPrivilegeCreateRequest;
import com.wuji.service.model.request.FormPrivilegeUpdateRequest;
import com.wuji.service.model.vo.FormPrivilegeConfigVO;
import com.wuji.service.model.vo.FormPrivilegeDetailVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormPrivilegeConverter {
    public static final AbstractFormPrivilegeConverter INSTANCE =
            Mappers.getMapper(AbstractFormPrivilegeConverter.class);

    public abstract FormPrivilegeEntity toEntity(FormPrivilegeCreateRequest formPrivilegeCreateRequest);

    public abstract FormPrivilegeEntity toEntity(TemplateFormPrivilegeVO templateFormPrivilegeVO);

    public abstract FormPrivilegeEntity toEntity(FormPrivilegeUpdateRequest formPrivilegeUpdateRequest);

    public abstract FormPrivilegeVO toVO(FormPrivilegeEntity formPrivilegeEntity);

    public abstract FormPrivilegeDetailVO toDetailVO(FormPrivilegeEntity formPrivilegeEntity);

    public abstract FormPrivilegeConfigVO toVO(FormPrivilegeVO formPrivilegeVO);
}
