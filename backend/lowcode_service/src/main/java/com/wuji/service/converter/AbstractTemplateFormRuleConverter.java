package com.wuji.service.converter;

import com.wuji.service.model.entity.TemplateFormRuleEntity;
import com.wuji.service.model.vo.FormRuleVO;
import com.wuji.service.model.vo.TemplateFormRuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractTemplateFormRuleConverter {

    public static final AbstractTemplateFormRuleConverter INSTANCE =
            Mappers.getMapper(AbstractTemplateFormRuleConverter.class);

    public abstract TemplateFormRuleEntity toEntity(FormRuleVO formRuleVO);

    public abstract TemplateFormRuleVO toVO(TemplateFormRuleEntity templateFormRuleEntity);

}
