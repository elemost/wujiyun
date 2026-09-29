package com.wuji.service.converter;

import com.wuji.service.model.info.FormAggregateTableField;
import com.wuji.service.model.info.MongodbSearchField;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractAggregateTableConverter {
    public static final AbstractAggregateTableConverter INSTANCE =
            Mappers.getMapper(AbstractAggregateTableConverter.class);

    public abstract MongodbSearchField toSearchField(FormAggregateTableField formAggregateTableField);
}
