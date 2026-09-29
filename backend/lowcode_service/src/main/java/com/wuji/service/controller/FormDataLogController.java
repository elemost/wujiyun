package com.wuji.service.controller;

import com.wuji.service.model.vo.FormDataLogVO;
import com.wuji.service.service.FormDataLogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 表单数据日志 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-09-23
 */
@RestController
@RequestMapping("/form/data/log")
public class FormDataLogController {
    @Autowired
    private FormDataLogService formDataLogService;

    @ApiOperation("变更记录")
    @GetMapping("/list/{uuid}")
    public List<FormDataLogVO> list(@PathVariable String uuid, @RequestParam("applicationId") String applicationId){
        return formDataLogService.list(uuid, applicationId);
    }
}
