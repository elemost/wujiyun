package com.wuji.service.model.request;

import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.request.publish.PublicPublishSecretRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FormMongodbLinkSelectRequest extends PublicPublishSecretRequest {
    private String fieldType;

    private String fieldId;

    private String childFieldId;

    private String formId;

    private MongodbSearchFilter filter;

    private String applicationId;

    private List<MongoSort> sorts;
}
