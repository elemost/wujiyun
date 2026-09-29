package com.wuji.service.model.request;

import com.wuji.common.model.request.BasePageRequest;
import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FormViewMongoDbRequest extends BasePageRequest {
    private String formId;

    private String keyword;

    private MongodbSearchFilter filter;

    private String applicationId;

    private List<MongoSort> sorts;

    private List<String> keyList;

    private String groupId;

    private List<String> uuidList;
}
