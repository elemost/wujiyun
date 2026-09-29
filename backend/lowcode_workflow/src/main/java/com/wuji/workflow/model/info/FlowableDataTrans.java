package com.wuji.workflow.model.info;

import lombok.Data;

@Data
public class FlowableDataTrans {
    private String sourceFieldId;

    private String targetFieldId;

    private String targetType;

    private String sourceType;

    private String sourceParentFieldId;

    private String targetParentFieldId;


}
