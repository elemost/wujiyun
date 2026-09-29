package com.wuji.workflow.controller;

import com.wuji.common.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import org.flowable.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private IdentityService identityService;

    @ApiOperation("获取公司列表")
    @GetMapping("/list")
    public Object list() {
        return identityService.createUserQuery().tenantId(UserUtils.getUser().getCompanyId().toString())
                .listPage(1, 10);
    }
}
