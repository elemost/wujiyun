package com.wuji.service.model.request;

import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.MongodbWidget;
import lombok.Data;

@Data
public class MongodbAggregateRequest {
    private MongodbWidget widget;

    private MongodbSearchFilter filter;

    private String formId;

    private String applicationId;

}
