package com.wuji.service.controller;

import com.wuji.service.service.ApplicationDataService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application/data")
public class ApplicationDataController {

    @Autowired
    private ApplicationDataService applicationDataService;

    @ApiOperation("删除数据")
    @PostMapping("/delete/{applicationId}")
    public void delete(@PathVariable String applicationId) {
        applicationDataService.clearAllData(applicationId);
    }

    @ApiOperation("删除数据")
    @PostMapping("/deleteFormData")
    public void delete(@RequestParam String applicationId, @RequestParam String formId) {
        applicationDataService.clearAllData(applicationId, formId);
    }

    @ApiOperation("数据迁移")
    @PostMapping("/migrateData/{applicationId}")
    public void migrateData(@PathVariable String applicationId) {
        applicationDataService.migrateData(applicationId);
    }

}
