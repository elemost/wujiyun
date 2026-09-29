package com.wuji.open.converter;

import com.wuji.open.model.vo.ApplicationFormOpenVO;
import com.wuji.open.model.vo.ApplicationOpenVO;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.ApplicationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractApplicationOpenConverter {
    public static final AbstractApplicationOpenConverter INSTANCE =
            Mappers.getMapper(AbstractApplicationOpenConverter.class);

    public abstract ApplicationOpenVO toVO(ApplicationVO application);

    @Mapping(source = "categoryName", target = "formName")
    @Mapping(source = "categoryType", target = "formType")
    public abstract ApplicationFormOpenVO toVO(ApplicationCategoryVO applicationCategoryVO);

}
