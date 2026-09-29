package com.wuji.service.service;

import com.wuji.service.model.request.FormDataDownloadRequest;

import javax.servlet.http.HttpServletResponse;

public interface FormExtraFunctionTemplateService extends FormExtraFunctionService {
    void downloadExcel(FormDataDownloadRequest formDataDownloadRequest, HttpServletResponse httpServletResponse);

    void downloadExcelTest(FormDataDownloadRequest formDataDownloadRequest, HttpServletResponse httpServletResponse);

    void downloadWord(FormDataDownloadRequest formDataDownloadRequest, HttpServletResponse httpServletResponse);

    void downloadWordTest(FormDataDownloadRequest formDataDownloadRequest, HttpServletResponse httpServletResponse);
}
