package com.wuji.workflow.controller;

import com.wuji.workflow.service.FlowableCopyUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 抄送对象表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-09-12
 */
@RestController
@RequestMapping("/flowable/copy/user")
public class FlowableCopyUserController {

    @Autowired
    private FlowableCopyUserService flowableCopyUserService;

    @ApiOperation("查看抄送")
    @GetMapping("/view/{copyId}")
    public void view(@PathVariable String copyId) {
        flowableCopyUserService.view(copyId);
    }
}
