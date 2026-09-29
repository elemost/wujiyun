package com.wuji.service.controller;

import com.wuji.service.model.request.FormPublicPublishSaveRequest;
import com.wuji.service.model.vo.FormPublicPublishVO;
import com.wuji.service.service.FormPublicPublishService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-02-10
 */
@RestController
@RequestMapping("/form/public/publish")
public class FormPublicPublishController {

    @Autowired
    private FormPublicPublishService formPublicPublishService;

    @ApiOperation("表单公开发布保存")
    @PostMapping("/save")
    public FormPublicPublishVO save(@RequestBody FormPublicPublishSaveRequest formPublicPublishSaveRequest) {
        return formPublicPublishService.save(formPublicPublishSaveRequest);
    }


    @ApiOperation("表单发布详情")
    @GetMapping("/publish/info/{formId}")
    public FormPublicPublishVO publishInfo(@PathVariable String formId,
                                           @RequestParam("applicationId") String applicationId,
                                           @RequestParam("publishType") String publishType) {
        return formPublicPublishService.info(applicationId, formId, publishType);
    }

}
