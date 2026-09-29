package com.wuji.service.controller;

import com.wuji.common.cache.ConfigCache;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.model.Response;
import com.wuji.common.utils.TimeUtils;
import com.wuji.service.enums.ApplicationInfoKeyEnum;
import com.wuji.service.service.ApplicationInfoService;
import com.wuji.service.service.TemplateApplicationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 应用 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
@RestController
@RequestMapping("/template/application")
public class TemplateApplicationController {

    @Autowired
    private TemplateApplicationService templateApplicationService;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @ApiOperation("生成模版")
    @GetMapping("/generateTemplate/{applicationId}")
    public Response<String> generateTemplate(@PathVariable String applicationId) {
        return Response.success(templateApplicationService.generateTemplate(applicationId));
    }

    @ApiOperation("生成模版")
    @GetMapping("/generateTemplateByTemplateId/{applicationId}")
    public Response<String> generateTemplateByTemplateId(@PathVariable String applicationId) {
        return Response.success(templateApplicationService.generateTemplateByTemplateId(applicationId));
    }

    // @ApiOperation("使用模版")
    // @GetMapping("/useTemplate/{application}")
    // @ClientFunction(resourceCode = ResourceCodeConstants.APPLICATION_COUNT,
    //         resourceValidator = ApplicationCountValidator.class)
    // public Response<String> useTemplate(@PathVariable String application,
    //                                     @RequestParam(value = "needData", required = false, defaultValue = "1")
    //                                     Boolean needData) {
    //     return Response.success(templateApplicationService.useTemplate(application, needData));
    // }

    @ApiOperation("安装模版")
    @GetMapping("/install/{templateId}")
    public Response<String> install(@PathVariable String templateId) {
        String applicationId = templateApplicationService.useTemplate(templateId, Boolean.TRUE);
        addApplicationInfo(applicationId);
        return Response.success(templateId);
    }

    // @ApiOperation("模板列表")
    // @PostMapping("/queryList")
    // public QueryPageVO<TemplateApplicationVO> queryList(
    //         @RequestBody TemplateApplicationRequest templateApplicationRequest) {
    //     return templateApplicationService.queryList(templateApplicationRequest);
    // }

    // @ApiOperation("模板详情")
    // @GetMapping("/info/{id}")
    // public TemplateApplicationVO info(@PathVariable String id) {
    //     return templateApplicationService.info(id);
    // }
    //

    private void addApplicationInfo(String applicationId) {
        int day = Integer.parseInt(
                Objects.requireNonNull(ConfigCache.getValue(ConfigEnum.APPLICATION_EXPIRE_DAY.name())));
        long time = TimeUtils.getDataZero(new Date(), day).getTime();
        applicationInfoService.insert(applicationId, ApplicationInfoKeyEnum.EXPIRE_TIME.name(), Long.toString(time));
    }
}
