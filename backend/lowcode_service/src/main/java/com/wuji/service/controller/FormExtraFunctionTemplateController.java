package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.constant.ResourceCodeConstants;
import com.wuji.service.enums.FormExtraFunctionTypeEnum;
import com.wuji.service.model.request.FormDataDownloadRequest;
import com.wuji.service.model.request.FormExtraFunctionCreateRequest;
import com.wuji.service.model.request.FormExtraFunctionUpdateRequest;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.service.FormExtraFunctionTemplateService;
import com.wuji.service.service.FormTemplateMoreService;
import com.wuji.service.valid.FunctionCommonValidator;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/form/extra/function/template")
public class FormExtraFunctionTemplateController {

    @Autowired
    private FormExtraFunctionTemplateService formExtraFunctionTemplateService;

    @Autowired
    private FormTemplateMoreService formTemplateMoreService;

    @ApiOperation("创建")
    @PostMapping("/create")
    @ClientFunction(resourceCode = ResourceCodeConstants.TEMPLATE_PRINT,
            resourceValidator = FunctionCommonValidator.class, resourceName = "新增打印模板",
            applicationLocation = IdentifierLocationEnum.BODY_FIELD)
    public Response<String> create(@RequestBody FormExtraFunctionCreateRequest formExtraFunctionCreateRequest) {
        formExtraFunctionCreateRequest.setFunctionType(FormExtraFunctionTypeEnum.CUSTOM_TEMPLATE.name());
        String id = formExtraFunctionTemplateService.create(formExtraFunctionCreateRequest);
        return Response.success(id);
    }

    @ApiOperation("通过表单id获取数据")
    @GetMapping("/getByFormId/{applicationId}/{formId}")
    public List<FormExtraFunctionVO> getByFormId(@PathVariable String formId, @PathVariable String applicationId) {
        return formExtraFunctionTemplateService.getByFormId(formId, applicationId,
                FormExtraFunctionTypeEnum.CUSTOM_TEMPLATE.name());
    }

    @ApiOperation("编辑")
    @PostMapping("/update")
    public void update(@RequestBody FormExtraFunctionUpdateRequest formExtraFunctionUpdateRequest) {
        formExtraFunctionUpdateRequest.setFunctionType(FormExtraFunctionTypeEnum.CUSTOM_TEMPLATE.name());
        formExtraFunctionTemplateService.update(formExtraFunctionUpdateRequest);
    }

    @ApiOperation("根据模板导出数据")
    @PostMapping("/downloadExcel")
    @ClientFunction(resourceCode = ResourceCodeConstants.TEMPLATE_PRINT,
            resourceValidator = FunctionCommonValidator.class, resourceName = "使用打印模板",
            applicationLocation = IdentifierLocationEnum.BODY_FIELD)
    public void download(@RequestBody FormDataDownloadRequest formDataDownloadRequest,
                         HttpServletResponse httpServletResponse) {
        formExtraFunctionTemplateService.downloadExcel(formDataDownloadRequest, httpServletResponse);
    }

    @ApiOperation("根据模板导出数据")
    @PostMapping("/downloadExcelTest")
    public void downloadExcelTest(@RequestBody FormDataDownloadRequest formDataDownloadRequest,
                                  HttpServletResponse httpServletResponse) {
        formExtraFunctionTemplateService.downloadExcelTest(formDataDownloadRequest, httpServletResponse);
    }


    @ApiOperation("根据模板导出数据")
    @PostMapping("/downloadWord")
    @ClientFunction(resourceCode = ResourceCodeConstants.TEMPLATE_PRINT,
            resourceValidator = FunctionCommonValidator.class, resourceName = "使用打印模板",
            applicationLocation = IdentifierLocationEnum.BODY_FIELD)
    public void downloadWord(@RequestBody FormDataDownloadRequest formDataDownloadRequest,
                             HttpServletResponse httpServletResponse) {
        formExtraFunctionTemplateService.downloadWord(formDataDownloadRequest, httpServletResponse);
    }

    @ApiOperation("根据模板导出数据")
    @PostMapping("/downloadWordMore")
    public void downloadWordMore(@RequestBody FormDataDownloadRequest formDataDownloadRequest,
                                 HttpServletResponse httpServletResponse) {
        formTemplateMoreService.downloadWordMore(formDataDownloadRequest, httpServletResponse);
    }

    @ApiOperation("根据模板导出数据")
    @PostMapping("/downloadExcelMore")
    public void downloadExcelMore(@RequestBody FormDataDownloadRequest formDataDownloadRequest,
                                  HttpServletResponse httpServletResponse) {
        formTemplateMoreService.downloadExcelMore(formDataDownloadRequest, httpServletResponse);
    }


    @ApiOperation("根据模板导出数据")
    @PostMapping("/downloadWordTest")
    public void downloadWordTest(@RequestBody FormDataDownloadRequest formDataDownloadRequest,
                                 HttpServletResponse httpServletResponse) {
        formExtraFunctionTemplateService.downloadWordTest(formDataDownloadRequest, httpServletResponse);
    }

}
