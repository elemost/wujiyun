package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FormRuleConditionRel {
    private List<FormRuleCondition> relates;

    private String rel;

}
