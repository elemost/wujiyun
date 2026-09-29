package com.wuji.service.controller;

import com.wuji.service.model.request.FormExtraFunctionTitleSaveRequest;
import com.wuji.service.service.FormExtraFunctionTitleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form/extra/function/title")
public class FormExtraFunctionTitleController {

    @Autowired
    private FormExtraFunctionTitleService formExtraFunctionTitleService;


    @ApiOperation("保存")
    @PostMapping("/save")
    public void save(@RequestBody FormExtraFunctionTitleSaveRequest formExtraFunctionTitleSaveRequest) {
        formExtraFunctionTitleService.saveTitleConfig(formExtraFunctionTitleSaveRequest);
    }


}
