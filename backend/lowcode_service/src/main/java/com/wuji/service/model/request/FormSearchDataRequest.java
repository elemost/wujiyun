package com.wuji.service.model.request;

import com.wuji.common.privilege.annotation.ApplicationId;
import com.wuji.common.privilege.annotation.ResourceId;
import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.request.publish.PublicPublishSecretRequest;
import lombok.Data;

import java.util.List;

@Data
public class FormSearchDataRequest extends PublicPublishSecretRequest {


    private String formId;

    private String keyword;

    private List<String> keyList;

    private MongodbSearchFilter filter;

    private String groupId;

    private String parentDataUuid;

    @ResourceId
    private String parentFormId;

    @ApplicationId
    private String applicationId;

    private List<MongoSort> sorts;

    private String uuid;

    private MongodbSearchFilter viewFilter;

    private MongodbSearchFilter treeFilter;

    private List<FormFieldRequest> fields;

    private List<String> uuidList;

    private String status;
}
