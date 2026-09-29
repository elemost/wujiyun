package com.wuji.service.service;

import com.wuji.service.model.info.FormAggregateTable;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.vo.FormAggregateMongoVO;

public interface AggregateTableService {
    Object aggregateTable(FormAggregateTable formAggregateTable, MongodbSearchFilter filter);

    FormAggregateMongoVO buildAggregate(FormAggregateTable formAggregateTable);

}
