package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormRuleCreateRequest {
    private String formId;

    private String applicationId;

    private String ruleName;

    private String ruleConfig;

    private String ruleType;

    @ApiModelProperty("状态")
    private String state;
}
