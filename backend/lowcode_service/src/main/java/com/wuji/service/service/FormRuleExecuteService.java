package com.wuji.service.service;

import com.alibaba.fastjson.JSONObject;
import com.wuji.service.model.info.FormRuleConditionRel;
import com.wuji.service.model.request.FormRuleActionCheckRequest;
import com.wuji.service.model.vo.FormRuleVO;

public interface FormRuleExecuteService {
    void checkVerifySubmit(String applicationId, String formId, String uuid, JSONObject instValue);

    boolean isExecuteResult(JSONObject instValue, FormRuleVO formRuleVO, FormRuleConditionRel matchRule);

   Boolean checkWhileAction(FormRuleActionCheckRequest formRuleActionCheckRequest);
}
