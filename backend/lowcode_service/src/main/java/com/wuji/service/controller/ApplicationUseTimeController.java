package com.wuji.service.controller;

import com.wuji.service.model.request.ApplicationUseTimeSaveRequest;
import com.wuji.service.service.ApplicationUseTimeService;
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
 * @since 2024-09-04
 */
@RestController
@RequestMapping("/application/use/time")
public class ApplicationUseTimeController {

    @Autowired
    private ApplicationUseTimeService applicationUseTimeService;

    @ApiOperation("使用应用")
    @PostMapping("/save")
    public void save(@RequestBody ApplicationUseTimeSaveRequest applicationUseTimeSaveRequest) {
        applicationUseTimeService.save(applicationUseTimeSaveRequest.getApplicationId());
    }
}
