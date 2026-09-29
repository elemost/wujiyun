package com.wuji.workflow.model.request;

import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import lombok.Data;

@Data
public class ModelRequest {

    private String modelId;
    private String name;
    private String key;
    private String applicationId;
    private String description;
    private String bpmnXml;
    private Boolean newVersion;
    private String tenantId;
    private FormModelDesignerDomain config;
}
