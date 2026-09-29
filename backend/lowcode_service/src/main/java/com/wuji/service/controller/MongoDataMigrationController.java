package com.wuji.service.controller;

import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.MongoDataMigrationService;
import com.wuji.service.service.TemplateApplicationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/mongo/data/migration")
public class MongoDataMigrationController {

    @Autowired
    private MongoDataMigrationService mongoDataMigrationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private TemplateApplicationService templateApplicationService;

    @ApiOperation("模板详细同步")
    @GetMapping("/templateInfo")
    public void templateInfo() {
        mongoDataMigrationService.templateInfo();
    }

    @ApiOperation("公司同步")
    @GetMapping("/companyInfo")
    public void companyInfo() {
        mongoDataMigrationService.companyInfo();
    }



    // @ApiOperation("dealIcon")
    // @GetMapping("/dealIcon")
    // public void dealIcon() {
    //     applicationService.dealIcon();
    //     templateApplicationService.dealIcon();
    // }

}
