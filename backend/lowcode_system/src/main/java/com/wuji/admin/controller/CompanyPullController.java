package com.wuji.admin.controller;

import com.wuji.admin.service.CompanyPullService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company/pull")
public class CompanyPullController {

    @Autowired
    private CompanyPullService companyPullService;

    @ApiOperation("获取帐号的thirdId")
    @PostMapping("/thirdId")
    public void pullThirdId() {
        companyPullService.pullThirdId();
    }


}
