package com.wuji.service.controller;

import com.wuji.service.model.vo.FormPublishVO;
import com.wuji.service.service.FormPublishService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 表单发布表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-08-20
 */
@RestController
@RequestMapping("/form/publish")
public class FormPublishController {

    @Autowired
    private FormPublishService formPublishService;

    @ApiOperation("表单详情")
    @GetMapping("/info/{categoryId}")
    public FormPublishVO info(@PathVariable String categoryId) {
        return formPublishService.info(categoryId, null);
    }

}