package com.wuji.service.model.request;

import com.wuji.service.model.info.MongodbSearchField;
import lombok.Data;

import java.util.List;

@Data
public class MongodbDetailedCheckRequest {
    private String formula;

    private String applicationId;

    private String formId;

    private List<MongodbSearchField> fields;
}
