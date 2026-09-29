package com.wuji.service.model.vo;

import com.wuji.service.model.info.FormAggregateTableField;
import com.wuji.service.model.info.FormAggregateTableRelation;
import com.wuji.service.model.info.FormAggregateTableValField;
import com.wuji.service.model.info.MongodbAggregateData;
import lombok.Data;

import java.util.List;

@Data
public class AggregateTableAggregateVO {
    private MongodbAggregateData mongodbAggregateData;

    private List<FormAggregateTableValField> valFields;

    private List<FormAggregateTableRelation> relations;

    private List<FormAggregateTableField> fieldXs;

    private List<FormAggregateTableField> fieldYs;

    private List<FormAggregateTableField> joinedFields;
}
