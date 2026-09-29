package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.service.model.request.FormModelCreateRequest;
import com.wuji.service.model.request.FormModelUpdateStatusRequest;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.service.service.FormModelService;
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
 * 流程表单绑定表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-09-03
 */
@RestController
@RequestMapping("/form/model")
public class FormModelController {

    @Autowired
    private FormModelService formModelService;

    @ApiOperation("开始使用--创建模型")
    @PostMapping("/create")
    public FormModelVO createModel(@RequestBody FormModelCreateRequest formModelCreateRequest) {
        return formModelService.createModel(formModelCreateRequest);
    }

    @ApiOperation("模型详情")
    @GetMapping("/info/{formId}")
    public FormModelVO info(@PathVariable String formId, @RequestParam("applicationId") String applicationId) {
        return formModelService.info(formId, applicationId);
    }


    @ApiOperation("发布启用流程")
    @GetMapping("/publishOrBind/{modelId}/{formId}")
    public void publishOrBind(@PathVariable String formId, @PathVariable String modelId,
                              @RequestParam("applicationId") String applicationId) {
        formModelService.publishOrBind(modelId, formId, applicationId);
    }

    @ApiOperation("新版本")
    @GetMapping("/newVersion/{modelId}")
    public Response<String> newVersion(@PathVariable String modelId) {
        return Response.success(formModelService.newVersion(modelId));
    }


    @ApiOperation("修改状态")
    @PostMapping("/updateStatus")
    public void updateStatus(@RequestBody FormModelUpdateStatusRequest formModelUpdateStatusRequest) {
        formModelService.updateStatus(formModelUpdateStatusRequest);
    }
}
