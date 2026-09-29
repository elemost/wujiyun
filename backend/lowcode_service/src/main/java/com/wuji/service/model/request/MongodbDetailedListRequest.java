package com.wuji.service.model.request;

import com.wuji.common.model.request.BasePageRequest;
import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.MongodbWidget;
import lombok.Data;

import java.util.List;

@Data
public class MongodbDetailedListRequest extends BasePageRequest {
    private String formId;

    private MongodbWidget widget;

    private MongodbSearchFilter filter;

    private List<MongoSort> sorts;

    private String applicationId;

}
