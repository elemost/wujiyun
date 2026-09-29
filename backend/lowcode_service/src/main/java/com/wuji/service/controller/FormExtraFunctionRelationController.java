package com.wuji.service.controller;

import com.wuji.service.model.request.FormExtraFunctionRelationSaveRequest;
import com.wuji.service.service.FormExtraFunctionRelationService;
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
 * @since 2024-12-25
 */
@RestController
@RequestMapping("/form/extra/function/relation")
public class FormExtraFunctionRelationController {

    @Autowired
    private FormExtraFunctionRelationService formExtraFunctionRelationService;

    @ApiOperation("保存")
    @PostMapping("/save")
    public void save(@RequestBody FormExtraFunctionRelationSaveRequest formExtraFunctionRelationSaveRequest) {
        formExtraFunctionRelationService.save(formExtraFunctionRelationSaveRequest.getFunctionId(),
                formExtraFunctionRelationSaveRequest.getBusinessType(),
                formExtraFunctionRelationSaveRequest.getBusinessIdList());
    }
}
