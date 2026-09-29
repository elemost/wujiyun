package com.wuji.service.controller;

import com.wuji.service.model.request.FormSerialNumberRequest;
import com.wuji.service.model.request.FormSerialNumberResetRequest;
import com.wuji.service.model.vo.FormSerialNumberVO;
import com.wuji.service.service.FormSerialNumberService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form/serial/number")
public class FormSerialNumberController {

    @Autowired
    private FormSerialNumberService formSerialNumberService;

    @ApiOperation("重置")
    @PostMapping("/reset")
    public void reset(@RequestBody FormSerialNumberResetRequest formSerialNumberResetRequest) {
        formSerialNumberService.reset(formSerialNumberResetRequest);
    }

    @ApiOperation("日期序列号详情")
    @PostMapping("/info")
    public FormSerialNumberVO info(@RequestBody FormSerialNumberRequest formSerialNumberRequest) {
        return formSerialNumberService.getInfo(formSerialNumberRequest);
    }

}
