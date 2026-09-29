package com.wuji.service.model.domain;

import com.wuji.workflow.model.info.FlowableFormFieldConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormWorkflowDataDomain {
    private String formId;

    private String applicationId;

    private List<String> uuids = new ArrayList<>();

    private String tableName;

    private List<FlowableFormFieldConfig> flowableFormFieldConfigs;
}
