package com.wuji.factory.converter;

import com.wuji.common.model.vo.DataFactoryReturnFieldCommonVO;
import com.wuji.common.model.vo.DataFactoryStageCommonFieldVO;
import com.wuji.factory.model.domain.DataFactoryField;
import com.wuji.factory.model.vo.DataFactoryStageFieldVO;
import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormDataFactoryExecuteConverter {

    public static final AbstractFormDataFactoryExecuteConverter INSTANCE =
            Mappers.getMapper(AbstractFormDataFactoryExecuteConverter.class);

    public abstract DataFactoryStageCommonFieldVO toVO(DataFactoryStageFieldVO dataFactoryStageFieldVO);

    public abstract DataFactoryReturnFieldCommonVO toVO(DataFactoryReturnFieldVO factoryReturnFieldVO);

    public abstract DataFactoryReturnFieldVO toVO(DataFactoryField dataFactoryField);

}
