package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.service.constant.ResourceCodeConstants;
import com.wuji.service.model.request.TemplateApplicationRequest;
import com.wuji.service.model.vo.TemplateApplicationVO;
import com.wuji.service.service.ApplicationOemService;
import com.wuji.service.valid.ApplicationCountValidator;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/template/application")
public class TemplateApplicationOemController {
    @Autowired
    private ApplicationOemService applicationOemService;

    @ApiOperation("模板详情")
    @GetMapping("/info/{id}")
    public TemplateApplicationVO info(@PathVariable String id) {
        return applicationOemService.info(id);
    }


    @ApiOperation("使用模版")
    @GetMapping("/useTemplate/{application}")
    @ClientFunction(resourceCode = ResourceCodeConstants.APPLICATION_COUNT,
            resourceValidator = ApplicationCountValidator.class)
    public Response<String> useTemplate(@PathVariable String application,
                                        @RequestParam(value = "needData", required = false, defaultValue = "1")
                                        Boolean needData) {
        return Response.success(applicationOemService.useTemplate(application, needData));
    }

    @ApiOperation("模板列表")
    @PostMapping("/queryList")
    public QueryPageVO<TemplateApplicationVO> queryList(
            @RequestBody TemplateApplicationRequest templateApplicationRequest) {
        return applicationOemService.queryList(templateApplicationRequest);
    }
}
