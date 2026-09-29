package com.wuji.service.model.request;

import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.request.publish.PublicPublishSecretRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FormMongoDbLinkRequest extends PublicPublishSecretRequest {
    private String formId;

    private MongodbSearchFilter filter;

    private List<String> fieldList;

    private List<String> childFieldList;

    private String applicationId;

    private List<MongoSort> sorts;

    private String keyword;

    private List<String> keyList;
}
