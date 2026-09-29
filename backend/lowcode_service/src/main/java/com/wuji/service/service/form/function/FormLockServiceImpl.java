package com.wuji.service.service.form.function;

import com.alibaba.fastjson.JSONObject;
import com.wuji.service.constant.Constants;
import com.wuji.service.enums.FormRuleStateEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.info.FormRuleLock;
import com.wuji.service.model.request.FormDataExtraParameterRequest;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormRuleVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormFunctionDataService;
import com.wuji.service.service.FormRuleExecuteService;
import com.wuji.service.service.FormRuleService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormLockServiceImpl implements FormFunctionDataService {

    @Autowired
    private FormRuleService formRuleService;

    @Autowired
    private FormRuleExecuteService formRuleExecuteService;

    @Override
    public String fieldType() {
        return "lock";
    }

    @Override
    public void dealFunctionReturn(List<LowcodeDataVO> lowcodeDataList,
                                   FormDataExtraParameterRequest formDataExtraParameterRequest, FormVO info) {
        List<FormPrivilegeVO> formPrivilegeVO = formDataExtraParameterRequest.getFormPrivilegeVO();
        if (CollectionUtils.isEmpty(formPrivilegeVO)) {
            return;
        }
        if (Constants.ADMIN_PRIVILEGE.equals(formPrivilegeVO.get(0).getId()) &&
                !formDataExtraParameterRequest.getFilterAdminPrivilege()) {
            return;
        }
        List<FormRuleVO> formRuleVOS =
                formRuleService.queryList(info.getId(), info.getApplicationId(), "LOCK", FormRuleStateEnum.UP.name());
        if (CollectionUtils.isEmpty(formRuleVOS)) {
            return;
        }
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            Map<String, String> lockMap = new HashMap<>();
            for (FormRuleVO formRuleVO : formRuleVOS) {
                FormRuleLock formRuleLock = JSONObject.parseObject(formRuleVO.getRuleConfig(), FormRuleLock.class);
                JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(lowcodeDataVO);
                boolean executeResult =
                        formRuleExecuteService.isExecuteResult(jsonObject, formRuleVO, formRuleLock.getMatchRule());
                if (executeResult) {
                    for (String button : formRuleLock.getButtons()) {
                        lockMap.put(button, formRuleLock.getRuleDesc());
                    }
                }
            }
            lowcodeDataVO.setLockMap(lockMap);
        }
    }
}
