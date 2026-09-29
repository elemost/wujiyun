package com.wuji.service.controller;

import com.wuji.service.model.request.FormDataDownloadRequest;
import com.wuji.service.service.FormTemplateMoreService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/form/info/template")
public class FormInfoTemplateController {

    @Autowired
    private FormTemplateMoreService formTemplateMoreService;

    @ApiOperation("根据模板导出数据")
    @PostMapping("/downloadWordMoreTest")
    public void downloadWordMoreTest(@RequestBody FormDataDownloadRequest formDataDownloadRequest,
                                     HttpServletResponse httpServletResponse) {
        formTemplateMoreService.downloadWordMoreTest(formDataDownloadRequest, httpServletResponse);
    }

    @ApiOperation("根据模板导出数据")
    @PostMapping("/downloadExcelMoreTest")
    public void downloadExcelMoreTest(@RequestBody FormDataDownloadRequest formDataDownloadRequest,
                                      HttpServletResponse httpServletResponse) {
        formTemplateMoreService.downloadExcelMoreTest(formDataDownloadRequest, httpServletResponse);
    }

}
