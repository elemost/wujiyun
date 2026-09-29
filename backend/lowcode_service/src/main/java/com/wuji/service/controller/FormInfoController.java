package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.service.model.request.FormInfoCopyRequest;
import com.wuji.service.model.request.FormInfoCreateRequest;
import com.wuji.service.model.request.FormInfoRequest;
import com.wuji.service.model.request.FormInfoSortRequest;
import com.wuji.service.model.request.FormInfoUpdateRequest;
import com.wuji.service.model.vo.FormInfoVO;
import com.wuji.service.service.FormInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
 * @since 2025-09-09
 */
@RestController
@RequestMapping("/form/info")
public class FormInfoController {

    @Autowired
    private FormInfoService formInfoService;

    @ApiOperation("新建表单详情")
    @PostMapping("/create")
    public Response<String> create(@RequestBody FormInfoCreateRequest formInfoCreateRequest) {
        return Response.success(formInfoService.create(formInfoCreateRequest));
    }

    @ApiOperation("修改表单详情")
    @PostMapping("/update")
    public void update(@RequestBody FormInfoUpdateRequest formInfoUpdateRequest) {
        formInfoService.update(formInfoUpdateRequest);
    }

    @ApiOperation("表单详情")
    @GetMapping("/info/{id}")
    public FormInfoVO info(@PathVariable String id, @RequestParam("applicationId") String applicationId,
                           @RequestParam("formId") String formId) {
        return formInfoService.info(id, applicationId, formId);
    }

    @ApiOperation("删除表单详情")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id, @RequestParam("applicationId") String applicationId,
                       @RequestParam("formId") String formId) {
        formInfoService.delete(id, applicationId, formId);
    }

    @ApiOperation("设置默认表单详情")
    @PutMapping("/setDefault/{id}")
    public void setDefault(@PathVariable String id, @RequestParam("applicationId") String applicationId,
                           @RequestParam("formId") String formId,
                           @RequestParam("defaultConfig") Boolean defaultConfig) {
        formInfoService.setDefault(id, applicationId, formId, defaultConfig);
    }

    @ApiOperation("设置默认表单详情")
    @PutMapping("/setEnable/{id}")
    public void setEnable(@PathVariable String id, @RequestParam("applicationId") String applicationId,
                          @RequestParam("formId") String formId, @RequestParam("enable") Boolean enable) {
        formInfoService.setEnable(id, applicationId, formId, enable);
    }


    @ApiOperation("排序")
    @PostMapping("/sort")
    public void sort(@RequestBody FormInfoSortRequest formInfoSortRequest) {
        formInfoService.sort(formInfoSortRequest);
    }

    @ApiOperation("获取表单默认详情")
    @GetMapping("/getDefault")
    public FormInfoVO getDefault(@RequestParam("applicationId") String applicationId,
                                 @RequestParam("formId") String formId) {
        return formInfoService.getDefault(applicationId, formId);
    }

    @ApiOperation("页面详情列表")
    @PostMapping("/queryList")
    public List<FormInfoVO> queryList(@RequestBody FormInfoRequest formInfoRequest) {
        return formInfoService.queryList(formInfoRequest);
    }

    @ApiOperation("复制")
    @PostMapping("/copy")
    public Response<String> copy(@RequestBody FormInfoCopyRequest formInfoCopyRequest) {
        return Response.success(formInfoService.copy(formInfoCopyRequest));
    }
}
