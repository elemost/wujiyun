package com.wuji.service.model.request;

import com.alibaba.fastjson.JSONObject;
import com.wuji.service.model.info.FormRuleConditionRel;
import lombok.Data;

@Data
public class FormRuleActionCheckRequest {
    private FormRuleConditionRel matchRule;

    private String applicationId;

    private JSONObject instValue;
}
