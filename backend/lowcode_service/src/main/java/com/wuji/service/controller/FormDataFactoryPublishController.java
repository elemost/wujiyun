package com.wuji.service.controller;

import com.wuji.service.model.vo.FormDataFactoryPublishVO;
import com.wuji.service.service.FormDataFactoryPublishService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2026-01-17
 */
@RestController
@RequestMapping("/form/data/factory/publish")
public class FormDataFactoryPublishController {

    @Autowired
    private FormDataFactoryPublishService formDataFactoryPublishService;

    @ApiOperation("变更记录")
    @GetMapping("/list/{applicationId}")
    public List<FormDataFactoryPublishVO> queryPublishList(@PathVariable String applicationId) {
        return formDataFactoryPublishService.queryPublishList(applicationId, null);
    }
}
