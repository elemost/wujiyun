package com.wuji.service.converter;

import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.request.factory.DataFactoryStageGroupFieldRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormDataFactoryExecuteConverter {

    public static final AbstractFormDataFactoryExecuteConverter INSTANCE =
            Mappers.getMapper(AbstractFormDataFactoryExecuteConverter.class);


    public abstract MongodbSearchField toField(DataFactoryStageGroupFieldRequest dataFactoryStageGroupFieldRequest);

}
