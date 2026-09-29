package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FormRuleVerify {
    private FormRuleConditionRel matchRule;

    // 错误提示
    private String promptContent;

    // 错误后动作 SUBMIT THROW
    private String afterErrorAction;

    // 是否时时提醒
    private Boolean constantReminder;

    private Boolean checkWhileSave;

    private List<String> specifiedField;
}
