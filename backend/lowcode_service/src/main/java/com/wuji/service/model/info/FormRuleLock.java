package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FormRuleLock {
    private FormRuleConditionRel matchRule;

    // 错误提示
    private String ruleDesc;

    private List<String> buttons;
}
