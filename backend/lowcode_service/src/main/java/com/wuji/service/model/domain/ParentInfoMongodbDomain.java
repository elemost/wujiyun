package com.wuji.service.model.domain;

import lombok.Data;

@Data
public class ParentInfoMongodbDomain {
    private String parentTaskId;

    private String parentProcessInstanceId;

    private String parentFormId;

    private String parentActivityId;

    private Boolean callBack = Boolean.FALSE;

    private String parentModelId;

    private String parentDataUuid;

    private String assigneeId;

    private String parentAssigneeId;
}
