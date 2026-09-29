package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamFlowableNode extends DataStreamCommon {

    private DataStreamConditionRel matchRule;

    private String formId;

    private String auditResult;

    private String targetKey;

    private String auditTaskKey;
}
