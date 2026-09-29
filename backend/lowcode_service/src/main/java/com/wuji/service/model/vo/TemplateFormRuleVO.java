package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateFormRuleVO {
    private String id;

    private String formId;

    private String applicationId;

    private String ruleName;

    private String ruleConfig;

    private String ruleType;

    @ApiModelProperty("状态")
    private String state;

    private Integer sort;
}
