package com.wuji.service.controller;

import com.wuji.service.model.request.FormRuleActionCheckRequest;
import com.wuji.service.service.FormRuleExecuteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form/rule/execute")
public class FormRuleExecuteController {

    @Autowired
    private FormRuleExecuteService formRuleExecuteService;

    @ApiOperation("交互校验")
    @PostMapping("/action/check")
    public Boolean checkWhileAction(@RequestBody FormRuleActionCheckRequest formRuleActionCheck) {
        return formRuleExecuteService.checkWhileAction(formRuleActionCheck);
    }

}
