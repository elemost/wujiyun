package com.wuji.service.converter;

import com.wuji.service.model.entity.FormRuleEntity;
import com.wuji.service.model.request.FormRuleCreateRequest;
import com.wuji.service.model.request.FormRuleUpdateRequest;
import com.wuji.service.model.vo.FormRuleVO;
import com.wuji.service.model.vo.TemplateFormRuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormRuleConverter {

    public static final AbstractFormRuleConverter INSTANCE = Mappers.getMapper(AbstractFormRuleConverter.class);

    public abstract FormRuleEntity toEntity(FormRuleCreateRequest formRuleCreateRequest);

    public abstract FormRuleEntity toEntity(FormRuleUpdateRequest formRuleUpdateRequest);

    public abstract FormRuleEntity toEntity(TemplateFormRuleVO templateFormRuleVO);

    public abstract FormRuleVO toVO(FormRuleEntity formRuleEntity);

    public abstract FormRuleEntity toEntity(FormRuleVO formRuleVO);

}
