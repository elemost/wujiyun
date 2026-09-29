package com.wuji.service.controller;

import com.wuji.service.enums.FormExtraFunctionTypeEnum;
import com.wuji.service.model.request.FormExtraFunctionCreateRequest;
import com.wuji.service.model.request.FormExtraFunctionUpdateRequest;
import com.wuji.service.service.FormExtraFunctionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-12-23
 */
@RestController
@RequestMapping("/form/extra/function")
public class FormExtraFunctionButtonController {


    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @ApiOperation("创建")
    @PostMapping("/create")
    public void create(@RequestBody FormExtraFunctionCreateRequest formExtraFunctionCreateRequest) {
        formExtraFunctionCreateRequest.setFunctionType(FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name());
        formExtraFunctionServiceImpl.create(formExtraFunctionCreateRequest);
    }

    @ApiOperation("编辑")
    @PostMapping("/update")
    public void update(@RequestBody FormExtraFunctionUpdateRequest formExtraFunctionUpdateRequest) {
        formExtraFunctionUpdateRequest.setFunctionType(FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name());
        formExtraFunctionServiceImpl.update(formExtraFunctionUpdateRequest);
    }

    @ApiOperation("生成二维码")
    @GetMapping("/generateQrcode/{id}")
    public void generateQrcode(@PathVariable String id, @RequestParam("dataUuid") String dataUuid,
                               HttpServletResponse httpServletResponse) {
        formExtraFunctionServiceImpl.generateQrcode(httpServletResponse, id, dataUuid);
    }
}
