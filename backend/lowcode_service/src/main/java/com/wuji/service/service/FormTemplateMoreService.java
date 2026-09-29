package com.wuji.service.service;

import com.wuji.service.model.request.FormDataDownloadRequest;

import javax.servlet.http.HttpServletResponse;

public interface FormTemplateMoreService {
    void downloadWordMoreTest(FormDataDownloadRequest formDataDownloadRequest, HttpServletResponse httpServletResponse);

    void downloadWordMore(FormDataDownloadRequest formDataDownloadRequest, HttpServletResponse httpServletResponse);

    void downloadExcelMoreTest(FormDataDownloadRequest formDataDownload, HttpServletResponse httpServletResponse);

    void downloadExcelMore(FormDataDownloadRequest formDataDownloadRequest, HttpServletResponse httpServletResponse);
}
