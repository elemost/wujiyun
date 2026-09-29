package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.service.model.request.FormDataFactoryCopyRequest;
import com.wuji.service.model.request.FormDataFactoryCreateRequest;
import com.wuji.service.model.request.FormDataFactoryRequest;
import com.wuji.service.model.request.FormDataFactorySyncConfigRequest;
import com.wuji.service.model.request.FormDataFactoryUpdateRequest;
import com.wuji.service.model.vo.FormDataFactoryUpdateVO;
import com.wuji.service.model.vo.FormDataFactoryVO;
import com.wuji.service.service.FormDataFactoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2026-01-05
 */
@RestController
@RequestMapping("/form/data/factory")
public class FormDataFactoryController {

    @Autowired
    private FormDataFactoryService formDataFactoryService;

    @ApiOperation("新建数据工厂")
    @PostMapping("/create")
    public Response<String> create(@RequestBody FormDataFactoryCreateRequest formDataFactoryCreateRequest) {
        return Response.success(formDataFactoryService.create(formDataFactoryCreateRequest));
    }

    @ApiOperation("修改数据工厂")
    @PostMapping("/update")
    public FormDataFactoryUpdateVO update(@RequestBody FormDataFactoryUpdateRequest formDataFactoryUpdateRequest) {
        return formDataFactoryService.update(formDataFactoryUpdateRequest);
    }

    @ApiOperation("复制数据工厂")
    @PostMapping("/copy")
    public Response<String> copy(@RequestBody FormDataFactoryCopyRequest formDataFactoryCopyRequest) {
        return Response.success(formDataFactoryService.copy(formDataFactoryCopyRequest));
    }


    @ApiOperation("数据工厂列表")
    @PostMapping("/queryList")
    public List<FormDataFactoryVO> queryList(@RequestBody FormDataFactoryRequest formDataFactoryRequest) {
        return formDataFactoryService.queryList(formDataFactoryRequest);
    }

    @ApiOperation("数据工厂详情")
    @GetMapping("/info/{id}")
    public FormDataFactoryVO info(@PathVariable String id, @RequestParam("applicationId") String applicationId) {
        return formDataFactoryService.info(id, applicationId);
    }

    @ApiOperation("数据工厂删除")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id, @RequestParam("applicationId") String applicationId) {
        formDataFactoryService.delete(id, applicationId);
    }

    @ApiOperation("同步配置")
    @PostMapping("/syncFormConfig")
    public void syncFormConfig(@RequestBody FormDataFactorySyncConfigRequest formDataFactorySyncConfigRequest) {
        formDataFactoryService.syncFormConfig(formDataFactorySyncConfigRequest);
    }
}
