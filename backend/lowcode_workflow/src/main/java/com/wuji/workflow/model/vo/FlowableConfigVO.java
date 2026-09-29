package com.wuji.workflow.model.vo;

import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import lombok.Data;

@Data
public class FlowableConfigVO {
    private String modelId;

    private String formId;

    private String status;

    private FormModelDesignerDomain config;
}
