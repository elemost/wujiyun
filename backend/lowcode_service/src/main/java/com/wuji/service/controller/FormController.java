package com.wuji.service.controller;

import com.wuji.service.model.request.FormUpdateRequest;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 表单 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
@RestController
@RequestMapping("/form")
public class FormController {

    @Autowired
    private FormService formService;

    @ApiOperation("表单详情")
    @GetMapping("/info/{id}")
    public FormVO detail(@PathVariable String id,
                         @RequestParam(value = "applicationId", required = false) String applicationId) {
        return formService.info(id, applicationId);
    }

    @ApiOperation("修改表单")
    @PutMapping("/update/{id}")
    public void updateForm(@RequestBody FormUpdateRequest formUpdateRequest) {
        formService.updateForm(formUpdateRequest);
    }

}
