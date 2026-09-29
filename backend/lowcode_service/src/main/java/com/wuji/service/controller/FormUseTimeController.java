package com.wuji.service.controller;

import com.wuji.service.model.request.FormUseTimeSaveRequest;
import com.wuji.service.service.FormUseTimeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-11-29
 */
@RestController
@RequestMapping("/form/use/time")
public class FormUseTimeController {

    @Autowired
    private FormUseTimeService formUseTimeService;

    @ApiOperation("使用表单")
    @PostMapping("/save")
    public void save(@RequestBody FormUseTimeSaveRequest formUseTimeSaveRequest) {
        formUseTimeService.save(formUseTimeSaveRequest.getFormId(), formUseTimeSaveRequest.getApplicationId());
    }
}
