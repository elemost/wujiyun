package com.wuji.systemapi.client.user.model;

import lombok.Data;

@Data
public class ParentInfoMongodbOpenDomain {
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
