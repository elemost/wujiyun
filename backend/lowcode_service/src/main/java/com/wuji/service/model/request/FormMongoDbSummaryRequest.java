package com.wuji.service.model.request;

import com.wuji.service.model.info.MongodbSearchFilter;
import lombok.Data;

import java.util.List;

@Data
public class FormMongoDbSummaryRequest {
    private String applicationId;

    private String formId;

    private String groupId;

    private MongodbSearchFilter filter;

    private List<FormMongoDbSummaryFieldRequest> fields;
}
