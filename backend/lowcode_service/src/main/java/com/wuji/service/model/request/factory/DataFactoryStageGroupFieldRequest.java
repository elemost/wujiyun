package com.wuji.service.model.request.factory;

import com.wuji.service.model.info.MongodbSearchField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataFactoryStageGroupFieldRequest extends MongodbSearchField {

    private String label;

    private String aliasName;
}
