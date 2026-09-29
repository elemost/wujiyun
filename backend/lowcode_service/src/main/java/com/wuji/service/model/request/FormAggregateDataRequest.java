package com.wuji.service.model.request;

import com.wuji.service.model.info.MongodbSearchFilter;
import lombok.Data;

@Data
public class FormAggregateDataRequest {
    private MongodbSearchFilter filter;

    private String applicationId;
}
