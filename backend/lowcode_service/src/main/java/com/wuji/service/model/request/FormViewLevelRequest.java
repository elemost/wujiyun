package com.wuji.service.model.request;

import com.wuji.service.model.info.MongodbSearchFilter;
import lombok.Data;

@Data
public class FormViewLevelRequest {
    private String formId;

    private String applicationId;

    private MongodbSearchFilter filter;

    private String groupId;

    private String parentFieldId;
}
