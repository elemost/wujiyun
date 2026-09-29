package com.wuji.service.model.request;

import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.MongodbSearchFilter;
import lombok.Data;

import java.util.List;

@Data
public class FormMongoDbExportRequest {
    private String formId;

    private String groupId;

    private String applicationId;

    private Boolean needData = Boolean.TRUE;

    private Boolean containUuid = Boolean.TRUE;

    private MongodbSearchFilter filter;

    private List<String> uuidList;

    private List<FormConfigCommon> configCommonList;
}
